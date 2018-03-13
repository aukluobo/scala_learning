package genomeTools

import scala.io.Source
import scala.util.matching.Regex
import java.io._
import java.util.zip._
import scala.collection.mutable.ListBuffer
import scala.math.Ordering

class calFaN50(fa:String) {

    def faName:String=fa
    
    private val gzType="""gz$""".r
    /*val gzType=new Regex("""gz$""")  */
    def fileType(s:String):Int = s match {
        case gzType() => 1
        case _ => 0
    }
    
    def gzipSourceStream(s:String)=Source.fromInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(s))))
    def plainSourceStream(s:String)=Source.fromFile(s)
    /*
    val bufferSource=try {
            Source.fromInputStream(gzipStream(fa))
        }catch{
            case e: FileNotFoundException => println("No such file "+fa)
            case e: IOException           => println("IO error")
            case _                        => println("unknow error")
        }
    val plainSource=plainSourceStream(fa)
    bufferSource.close()
    plainSource.close()
    */
    def calculateN(inputFa:String=fa):Unit={
        val inputSource=fileType(inputFa) match {
                case 1 => gzipSourceStream(inputFa)
                case 0 => plainSourceStream(inputFa)
            }
        var totalLength:Long=0
        var totalNumber:Long=0
        var partLength:Long=0
        val lengthList=new ListBuffer[Long]
        for(line <- inputSource.getLines()){
            val lineElem = line.toList
            lineElem match {
                case '>'::list => {
                                    totalNumber+=1
                                    lengthList+=partLength
                                    partLength=0
                                }
                case _         => {
                                    val aaLen=lineElem.length
                                    totalLength+=aaLen
                                    partLength+=aaLen
                                }
            }
        }
        lengthList+=partLength
        val lengthListSort=lengthList.sortWith(_>_).toList
        
        /* 
        val lengthListSort=lengthList.sorted.toList
        lengthList.sortWith lessThan  sortWith(_.compareTo(_) < 0)
        lengthList.sortBy f         def f[T](a:T,b:T):Boolean= a > b      sortBy(>)
        */
        val mapResult=collection.mutable.Map[String,Int]()
        var cutOff:Int=10
        var partNumber:Long=0
        var partTotal:Long=0
        println("total length "+totalLength)
        println("max length "+lengthListSort.head)
        for(len <- lengthListSort){
            partTotal+=len
            partNumber+=1
            val dv=partTotal*100/totalLength
            /*println("test "+len+"\t"+partTotal+"\t"+totalLength)*/
            if(dv<cutOff)
                1
            else{
                if(cutOff<100) println("N"+cutOff+"\t"+len+"\t"+partNumber+"\t"+partTotal)
                cutOff+=10
            }
        }
        inputSource.close()
    }
}


