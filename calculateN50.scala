import scala.io.Source
import scala.util.matching.Regex
import java.io._
import java.util.zip._
import scala.collection.mutable.ListBuffer

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
    def calculateN={
        val inputSource=fileType(fa) match {
                case 1 => gzipSourceStream(fa)
                case 0 => plainSourceStream(fa)
            }
        var totalLength:Int=0
        var totalNumber:Int=0
        var partLength:Int=0
        val lengthList=new ListBuffer[Int]
        for(line <- inputSource.getLines()){
            val lineElem = line.toList
            lineElem match {
                case ">"::list => {
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
        val lengthListSort=lengthList.sorted(>).toList
        
        /* 
        val lengthListSort=lengthList.sorted.toList
        lengthList.sortWith lessThan  sortWith(_.compareTo(_) < 0)
        lengthList.sortBy f         def f[T](a:T,b:T):Boolean= a > b      sortBy(>)
        */
        val mapResult=collection.mutable.Map[String,Int]()
        var cutOff=10
        var partNumber=0
        for(len <- lengthListSort){
            len*100/totalLength match {
                case _ < cutOff =>  partNumber+=1
                case _          =>  {
                                        println("N"+cutOff+"\t"+len+"\t"+partNumber)
                                        cutOff+=10
                                    }
            }
        }
    }
}

object calFaN50{
    def main(args:Array[String])={
        require(args.length>0)
        for(i <- args){
            val newCal=new calFaN50(i)
            println(newCal.faName)
            newCal.calculateN
        }
    }
}

