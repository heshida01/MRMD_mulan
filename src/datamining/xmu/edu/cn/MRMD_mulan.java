package datamining.xmu.edu.cn;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.RandomForest;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import weka.core.SelectedTag;


public class MRMD_mulan implements Runnable {

    static String [] classAttr;
    static double [][] feaData;
    static String [][] labelData;
    static List<Map.Entry<String, Double>> mrmrList;
    static String inputFile;
    static String outoputFile;
    static Calendar calendar = Calendar.getInstance();
    static int minutes = calendar.get(Calendar.MINUTE);
    static String arff = "out_"+ String.valueOf(minutes)+"."+"arff";
    static String csv="out_"+String.valueOf(minutes)+".csv";
    static int insNum = 0;
    static int feaNum = 0;
    static int labNum = 1;
    static int seleFeaNum = 0;
    static int disFunc = 1;
    static double bestRate=0;
    static double auc=0;
    static boolean isAuto = true;
    private static int optNum;
    private static String model="rf";
    private static double max=0;
    private static int count=0;
    //private static int low=0,high=0;
    private static Queue<String> list;     //storage temp file
    private static int np=1;      //num threading
    private static ConcurrentLinkedQueue<Integer> concurrentLinkedQueue;
    private static ConcurrentLinkedQueue<String> concurrentLinkedQueueF1;
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        // 测试命令
//String x="-i D://out.arff -o D://gjs1.txt -sn 20";
//args=x.split(" ");
//
       // long uptime1=System.currentTimeMillis();
        // Create a Parser
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("i", "input", true, "input file" );
        options.addOption("o", "output", true, "output file  score rank of each feature");
        options.addOption("df", "disFuc", true, "the distance function default(1)" );
        options.addOption("a", "arff", true, "outputfile of arff default (opt.arff)");
        options.addOption("m", "model", true, "opt model type defauly(rf)" );
        options.addOption("N", "No", false, "auto select" );
        options.addOption("sn", "sn", true, "select feature number");
        options.addOption("t", "thds", true, "the number of threadings");
        options.addOption("c", "csv", true, "make features'rate and f1 score into a csv file");
        // Parse the program arguments
        CommandLine commandLine = parser.parse( options, args );

        // Set the appropriate variables based on supplied options
        String file = "";


        if(commandLine.hasOption('N')) {
            isAuto= false;
        }

        if(commandLine.hasOption("sn")) {
            seleFeaNum = Integer.parseInt(commandLine.getOptionValue("sn"));

        }

        if(commandLine.hasOption('h') ) {
            print_help();
            System.exit(0);
        }
        if(!commandLine.hasOption('i') || !commandLine.hasOption('o')) {
            System.out.println("You should input -i , -o ");
            System.exit(0);
        }
        outoputFile = commandLine.getOptionValue('o');
        inputFile = commandLine.getOptionValue('i');
        if( commandLine.hasOption("a") ) {
            arff = commandLine.getOptionValue("a");
        }
        if( commandLine.hasOption("m") ) {
            model = commandLine.getOptionValue("m");
            if (model.equals("rf") || model.equals("svm") || model.equals("bagging") ||model.equals("N")){
                ;
            }else{
                System.out.println("model only can be rf,svm,bagging,N\n");
                System.exit(0);
            }



        }
        if( commandLine.hasOption("df") ) {
            disFunc = Integer.parseInt(commandLine.getOptionValue("df"));
            if(disFunc < 1 || disFunc > 4)
            {
                System.out.println("\r\nThe parameter of -df is error !! df={1, 2, 3, 4}\r\n");
            }
        }

        if(commandLine.hasOption("t")){
            np=Integer.parseInt(commandLine.getOptionValue("t"));
        }
        if(commandLine.hasOption("c")){
            csv=commandLine.getOptionValue("c");
        }

        File InputFile = new File(inputFile);
        if(!InputFile.exists())
        {
            System.out.println("Can't find input file: " + InputFile);
            System.exit(0);
        }
        BufferedReader InputBR = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile), "utf-8"));
        String InputLine = InputBR.readLine();

        String[] dataString;


        while(!InputLine.toUpperCase().contains("@DATA"))
        {
            InputLine = InputBR.readLine();
        }
        InputLine = InputBR.readLine();


        while(InputLine != null)
        {
            if(InputLine.equals("")){
                InputLine = InputBR.readLine();
                continue;
            }

            if(insNum==1)
                feaNum = InputLine.split(",").length;
            insNum ++;
            InputLine = InputBR.readLine();
        }


        InputBR.close();
        feaNum=feaNum-labNum;
        if(seleFeaNum==0){
            seleFeaNum = feaNum;
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        CompletionService<String> pool = new ExecutorCompletionService<String>(exec);
        String [][] inputData = new String[insNum][feaNum + labNum];
        getData gd = new getData(inputFile);
        gd.setData(inputData);
        gd.setFeaNum(feaNum);
        gd.setInsNum(labNum);
        gd.setLabNum(labNum);

        gd.run();
        //exec.execute(gd);



        double[] PearsonValue = new double[feaNum];
        Pearson pd = new Pearson(feaNum, labNum);
        pd.setInsNum(insNum);
        pd.setFeaNum(feaNum);
        pd.setLabNum(labNum);
        pd.setData(inputData);
        pd.setPearsonValue(PearsonValue);
        //pd.run();
        exec.execute(pd);

        labelData = new String[insNum][labNum];
        getLabel gl = new getLabel(inputData);
        gl.setData(labelData);
        gl.setFeaNum(feaNum);
        gl.setInsNum(insNum);
        gl.setLabNum(labNum);
        //gl.run();
        exec.execute(gl);

        feaData = new double[insNum][feaNum];
        getFeaData gfd = new getFeaData(inputData);
        gfd.setData(feaData);

        gfd.setFeaNum(feaNum);
        gfd.setInsNum(insNum);
        gfd.setLabNum(labNum);
        exec.execute(gfd);




        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        inputData = null;

        double[] CosineValue = new double[feaNum];
        Cosine cd = new Cosine(feaNum);
        cd.setCosineValue(CosineValue);
        cd.setData(feaData);
        cd.setFeaNum(feaNum);
        cd.setInsNum(insNum);
        cd.setLabNum(labNum);


        double[] EuclideanValue = new double[feaNum];
        Euclidean ed = new Euclidean(feaNum);
        ed.setData(feaData);
        ed.setEuclideanValue(EuclideanValue);
        ed.setFeaNum(feaNum);
        ed.setInsNum(insNum);
//		ed.setLabNum(labNum);

        double[] TanimotoValue = new double[feaNum];
        Tanimoto td = new Tanimoto(feaNum);
        td.setData(feaData);
        td.setTanimotoValue(TanimotoValue);;
        td.setFeaNum(feaNum);
        td.setInsNum(insNum);
//		td.setLabNum(labNum);
  //      MRMD_mulan mr1 = new MRMD_mulan();
    //    MRMD_mulan mr2 = new MRMD_mulan();

        double[] mrmrValue = new double[feaNum];
        switch (disFunc)
        {
            case 1:
                ed.run();
                for(int i = 0; i < feaNum; ++ i)
                {
                    mrmrValue[i] = EuclideanValue[i] + PearsonValue[i];
                }
                break;
            case 2:
                cd.run();
                for(int i = 0; i < feaNum; ++ i)
                {
                    mrmrValue[i] = CosineValue[i] + PearsonValue[i];
                }
                break;
            case 3:
                td.run();
                for(int i = 0; i < feaNum; ++ i)
                {
                    mrmrValue[i] = TanimotoValue[i] + PearsonValue[i];
                }
                break;
            case 4:
                ExecutorService exec2 = Executors.newFixedThreadPool(3);
                exec2.execute(ed);
                exec2.execute(cd);
                exec2.execute(td);
                exec2.shutdown();
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

                for(int i = 0; i < feaNum; ++ i)
                {
                    mrmrValue[i] = (PearsonValue[i] * 3 + EuclideanValue[i] + CosineValue[i] + TanimotoValue[i])/3;
                }
                break;
            default:
                break;
        }


        double max=0;
        for (int i=0;i<mrmrValue.length;i++){
            if (mrmrValue[i]>max){
                max=mrmrValue[i];
            }
        }

        for (int i=0;i<mrmrValue.length;i++){
            mrmrValue[i]=mrmrValue[i]/max;
        }



        Map<String, Double> mrmrMap = new HashMap<String, Double>();
        mrmrList = new ArrayList<Map.Entry<String, Double>>(mrmrMap.entrySet());
        mrmrList = initialHashMap(mrmrValue, feaNum);
        //System.out.println("mrmrlist=  "+mrmrList);



        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outoputFile), false), "utf-8"));
        bufferedWriter.write("The number of selected features is: " + seleFeaNum + "\r\n\r\n");
        bufferedWriter.write("The index of selected features start from 0" + "\r\n\r\n");
        bufferedWriter.write("NO." + "		" + "FeaName" + "		" + "Score" + "\r\n\r\n");
        int line = 1;
        for(int i = 0; i < seleFeaNum; ++ i)
        {
            bufferedWriter.write(line + "		" + mrmrList.get(i).getKey() + "		" + mrmrList.get(i).getValue() + "\r\n");
            line ++;
        }
        bufferedWriter.flush();
        bufferedWriter.close();



        classAttr = new String[labNum];
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile)), "utf-8"));
        String lineString = bufferedReader.readLine();
        while(!lineString.contains("@relation") && !lineString.contains("@RELATION") && !lineString.contains("@Relation"))
        {
            lineString = bufferedReader.readLine();
        }
        lineString = bufferedReader.readLine();
        while(lineString.length() == 0)
        {
            lineString = bufferedReader.readLine();
        }
        int count = 0;
        while(count < feaNum)
        {
            if(lineString.length() != 0)
            {
                count ++;
                lineString = bufferedReader.readLine();
            }
            else
            {
                lineString = bufferedReader.readLine();
            }
        }
        while(lineString.length() == 0)
        {
            lineString = bufferedReader.readLine();
        }
        count = 0;
        while(count < labNum)
        {
            if(lineString.length() != 0)
            {

                classAttr[count] = lineString;
                count ++;
                lineString = bufferedReader.readLine();
            }
            else
            {
                lineString = bufferedReader.readLine();
            }
        }

        bufferedReader.close();

        System.out.println("MRMD over.");
        System.out.println("Feature selction optimation begin");
        System.out.println("model:"+model);

        if(isAuto){
            concurrentLinkedQueueF1 = new ConcurrentLinkedQueue<String>();
            int num = optSelect();
            writeFeature(num,arff);
            System.out.println();
            System.out.println("The best feature number: "+String.valueOf(optNum));
            System.out.println("The best rate: "+String.valueOf(bestRate));
            System.out.println("Feature selction optimation end,the best arff save "+arff);
        }

        List<String> list1=new ArrayList<String>() ;
        // Collections.sort(concurrentLinkedQueueF1);
        for (String s:concurrentLinkedQueueF1){
            list1.add(s);
        }
        Collections.sort(list1, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String num1=o1.split(" ")[2];
                String num2=o2.split(" ")[2];
                //System.out.println("num1="+num1);
                //System.out.println("num2="+num2);
                if(Double.parseDouble(num1)>Double.parseDouble(num2)){
                    return 1;
                }
                else if(Double.parseDouble(num1)==Double.parseDouble(num2)){
                    return 0;
                }
                else
                    return  -1;
            }
        });



        writeCSV(list1,csv);
        for (String temp_file:list) {
            File del_file = new File(temp_file);
            Thread.sleep(100);
            if (del_file.exists() && del_file.isFile()) {
                if (del_file.delete()) {
                    System.out.println("delete " + del_file + " success!");
                } else {
                    System.out.println("delete " + del_file + " failed!");

                }
            } else {
                System.out.println("" + del_file + "not found!");
            }

        }
        //long uptime2 = System.currentTimeMillis();
        //long uptime=uptime2-uptime1;
       // System.out.print(TimeUnit.MILLISECONDS.toSeconds(uptime));


    }
    // feature num: 1 rate: 0.5443833464257659 ,f1weight: 0.5453777681563947
    public static void writeCSV(List<String> list,String csv) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, false)));
        String strhead="features"+","+"accuracy"+","+"F1\n";
        bufferedWriter.write(strhead);
        int i=1;
        for(String s:list){
            String Srate=s.split(" ")[4];
            String String_F1=s.split(":")[3];
            //System.out.println(String_F1);
            String strcontent="feature num"+Integer.toString(i)+","+Srate+","+String_F1+"\n";
            bufferedWriter.write(strcontent);
            i+=1;
        }
        bufferedWriter.close();
        System.out.println(csv+" file is finished!!!");
    }

    public static void print_help(){
        System.out.println("Usage: java -jar MRMD.jar" +
                "-i inputFile " +
                "-o outputFile \n" +
                "-df disFunc default(1) \n" +
                "-a arff default(opt.arff) \n" +
                "-model rf default(rf)  \n"+
                "-ps nps default(4)");
        System.out.println("[-i -o] is Necessary   [-sn -ln -df -a - model] is Optional ");
        System.exit(0);
    }


    public static int optSelect() throws Exception{
        ExecutorService exec3=Executors.newCachedThreadPool();
        initList();


        init_queue();
        for (int i = 0; i <np ; i++) {
            exec3.execute(new MRMD_mulan());
        }
        exec3.shutdown();
        exec3.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        max=getMax();
        optNum=getOptnum();
        bestRate=max;
        writeFeature(optNum,arff);
        //writeFeature(21,arff);
        return optNum;
    }

    public static void writeFeature(int seleFeaNum,String myarff) throws IOException{
        BufferedWriter arffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(myarff), false), "utf-8"));
        arffWriter.write("@relation " + (new File(inputFile)).getName() + "_feaSele");
        arffWriter.newLine();
        arffWriter.newLine();

        for(int i = 0; i < seleFeaNum; i ++)
        {
            arffWriter.write("@attribute Fea" + i + " numeric");
            arffWriter.newLine();
        }
        for(int i = 0; i < labNum; i ++)
        {
            arffWriter.write(classAttr[i]);
            arffWriter.newLine();
        }
        arffWriter.write("\r\n@data\r\n\r\n");

        //	System.out.println("insNum" + insNum);

        for(int i = 0; i < insNum; i ++)
        {

            for(int j = 0; j < seleFeaNum; j ++)
            {

                arffWriter.write(feaData[i][Integer.parseInt(mrmrList.get(j).getKey().substring(3, mrmrList.get(j).getKey().length()))] + ",");
                arffWriter.flush();

                // System.out.println("test "+feaData[i][Integer.parseInt(mrmrList.get(j).getKey().substring(3, mrmrList.get(j).getKey().length()))]);
            }

            for(int j = 0; j < labNum - 1; j ++)
            {
                arffWriter.write(labelData[i][j] + ",");
                arffWriter.flush();
            }
            arffWriter.write(labelData[i][labNum - 1]);
            arffWriter.flush();
            arffWriter.newLine();
        }

        arffWriter.flush();
        arffWriter.close();
    }


    public static Classifier getClassifier(String name) throws Exception {
        Classifier classify = null;

        int enum_type=1;
        if(name.equals("rf")) enum_type = 1;
        if(name.equals("svm")) enum_type = 2;
        if(name.equals("bagging")) enum_type = 3;

        switch (enum_type){
            case 1:
              //  String[] options = new String[4];

                classify=new RandomForest();
                //((RandomForest) classify).setOptions(weka.core.Utils.splitOptions("-I 100 -K 0   -S 1"));
                ((RandomForest) classify).setMaxDepth(0);
                ((RandomForest) classify).setNumTrees(100);
                ((RandomForest) classify).setNumFeatures(0);
                ((RandomForest) classify).setSeed(1);

                break;
            case 2:
                classify=new LibSVM();
                ((LibSVM) classify).setCacheSize(40.0);
                ((LibSVM) classify).setCoef0(0.0);
                ((LibSVM) classify).setCost(1.0);
                ((LibSVM) classify).setDegree(3);
                ((LibSVM) classify).setEps(0.001);
                ((LibSVM) classify).setGamma(0.0);
                ((LibSVM) classify).setKernelType(  new SelectedTag(2, LibSVM.TAGS_KERNELTYPE));
                ((LibSVM) classify).setDoNotReplaceMissingValues(false);
                ((LibSVM) classify).setLoss(0.1);
                ((LibSVM) classify).setNormalize(false);
                ((LibSVM) classify).setNu(0.5);
                ((LibSVM) classify).setProbabilityEstimates(false);
                ((LibSVM) classify).setShrinking(true);
                ((LibSVM) classify).setSVMType(new SelectedTag(0, LibSVM.TAGS_SVMTYPE));
                break;
            case 3:
                classify=new Bagging();
                ((Bagging) classify).setBagSizePercent(100);
                ((Bagging) classify).setCalcOutOfBag(false);
                ((Bagging) classify).setDebug(false);
                ((Bagging) classify).setNumExecutionSlots(1);
                ((Bagging) classify).setNumIterations(10);
                ((Bagging) classify).setSeed(1);
                break;
            default:
                System.out.println("目前只支持rf(randomForst),svm,bagging");
        }
        return classify;
    }



    public static double[] featureRate(String myarff) throws Exception{
        int folds=10;

        Classifier cls1 =getClassifier(model);

        //System.out.println("-----------------------------------"+"arff文件"+"的结果---------------------------------\n");

        Instances data = new Instances(new BufferedReader(new FileReader(myarff)));

        // set class attribute
        data.setClassIndex(data.numAttributes() - 1);

        // randomize data
        Random rand = new Random(1);
        Instances randData = new Instances(data);
        randData.randomize(rand);
        if (randData.classAttribute().isNominal())
            randData.stratify(folds);

        Evaluation evalAll = new Evaluation(randData);
        if(insNum<10){
            folds=insNum;
        }
        for (int n = 0; n < folds; n++) {
            Evaluation eval = new Evaluation(randData);
            Instances train = randData.trainCV(folds, n);
            Instances test = randData.testCV(folds, n);

            Classifier clsCopy = AbstractClassifier.makeCopy(cls1);
            clsCopy.buildClassifier(train);
            eval.evaluateModel(clsCopy, test);
            evalAll.evaluateModel(clsCopy, test);


        }
        double [] data1 = new double[2];
        data1[0]=evalAll.pctCorrect();
        data1[1]=evalAll.weightedFMeasure();

     return data1;
    }

    public static List initialHashMap(double data[], int feaNum)
    {
        Map<String, Double> mrmrMap = new HashMap<String, Double>();
        for(int i = 0; i < feaNum; ++ i)
        {
            mrmrMap.put("Fea" + i, data[i]);
        }

        List<Map.Entry<String, Double>> mrmrList = new ArrayList<Map.Entry<String, Double>>(mrmrMap.entrySet());
        Collections.sort(mrmrList, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
            {
//				return (o1.getValue()).compareTo(o2.getValue());
                if(Double.parseDouble(o1.getValue().toString()) < Double.parseDouble(o2.getValue().toString()))
                {
                    return 1;
                }
                else if(Double.parseDouble(o1.getValue().toString()) == Double.parseDouble(o2.getValue().toString()))
                {
                    return 0;
                }
                else {
                    return -1;
                }
            }

        });
       //System.out.println(mrmrList);
        return mrmrList;
    }



    @Override
    public void run()  {
        double temp = 0;

        String myarff;
        String T1=Thread.currentThread().getName().trim();
        long long_n=System.currentTimeMillis();     //临时文件名的一部分，时间戳，防止名字冲突
        String str=Long.toString(long_n);
        myarff="temp_"+T1+str+".arff";
        addFilepath(myarff);
        int i=0;
        while(!concurrentLinkedQueue.isEmpty()){
            i=concurrentLinkedQueue.remove();
            try {

                writeFeature(i,myarff);
                double [] data1 = new double[2];
                data1=featureRate(myarff);
                temp=data1[0];
                String s=("feature num: "+i+" rate: "+temp/100+" ,f1weight:"+data1[1]);

                concurrentLinkedQueueF1.add(s);
                countper();
                if(count<=seleFeaNum)
                    System.out.printf("\rcompleted: %3d/%d",count,seleFeaNum);  //自动优化进度
                //System.out.println();
                if(temp>this.max) {
                    setMax(temp);
                    setOptnum(i);
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public  static synchronized int getOptnum() {
        return optNum;
    }
    public  static synchronized int setOptnum(int i) {
        optNum = i;
        return optNum;
    }
    public static  synchronized double getMax() {
        return max;
    }
    public  static synchronized void setMax(double max1) {
        max = max1;
    }
    public static synchronized int countper(){
        count++;
        return count;
    }
    public  static void initList(){
        list=new LinkedList<String>();
    }
    public static  void init_queue(){
       concurrentLinkedQueue = new ConcurrentLinkedQueue<Integer>();

        for (int i = 1; i <= seleFeaNum; i++) {
            concurrentLinkedQueue.add(i);
        }
    }
    public  static synchronized  void addFilepath(String str){
        list.add(str);
    }
}
