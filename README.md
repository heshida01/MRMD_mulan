# mrmd_mulan
MRMD
     MRMD 特征选择方法主要有两部分决定的：
其一是特征和实例类标之间的相关性,MRMD用Pearson相关系数来计算特征和类标之间的相关性
其二是特征之间的冗余性,，用三种距离函数（Euclidean距离，Cosine距离和Tanimoto系数）来计算特征之间的冗余性。
Pearson相关系数越大说明特征与类标关系越紧密，距离越大说明特征之间的冗余性越低。最后，MRMD选出来的是和类标具有强相关并且特征之间具有低冗余性的特征子集 
Usage
mrmd.jar( Download the jar of MRMD )


java -jar mrmd.jar -i input -o output.txt


-i inputFile: 输入文件的绝对路径,目前只支持.arff文件格式。  
-o outputFile: 特征选择的具体特征打分和排序信息。  
可选参数  
（1）距离函数 -df ：  
the distance function default(1)，默认为1   
(1 = Euclidean Distance, 2 = Cosine Distance, 3 = Tanimoto coefficient, 4 = mean)  
（2）经过特征选择后的arff文件 -a ：  
outputfile of arff default (out.arff)  
（3）用于自动验证特征准确率的分类器 -m ：  
opt model type defauly(rf)   
（目前可支持： rf, svm, bagging，其中rf表示randomforest，  
（4）如果特征维度比较多的时候，可以只挑选部分特征进行自动化特征选择 -sn 1000  
（5） -N 表示不进行优化  
（6）-t 多线程（最大可设置为当前机器的线程数（默认为1）,查看线程数：grep 'processor' /proc/cpuinfo | sort -u | wc -l）  
 (7) -c (csv的文件名,输出accuracy和f1score,保存到csv文件)   
