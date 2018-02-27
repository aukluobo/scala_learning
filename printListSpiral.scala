import scala.collection.mutable.ListBuffer

object listSpiral{
    def main(args:Array[String])={
        val rawList:List[List[Int]]=List(List(1,2,3,4),List(5,6,7,8),List(9,10,11,12),List(13,14,15,16))
        val x:Int=0
        val y:Int=0
        val limit:Int=4
        def loopList(tempRawData:List[List[Int]],tempX:Int,tempY:Int,tempLimit:Int):Unit={
            val getList = new ListBuffer[Int]
            val headList = new ListBuffer[Int]
            def makeLoop():Unit={
                for(i <- 0 to tempLimit-1){
                    if(i==tempY){
                        for(p<-tempX to tempLimit-1-tempX){
                            val sn=tempRawData(i){p}
                            getList+=sn
                        }
                    }
                    if(i==(tempLimit-1-tempY)){
                        val m:List[Int] = List.range(tempX,tempLimit-tempX).reverse
                        for(j<-m){
                            val sn = tempRawData(i)(j)
                            getList+=sn
                        }
                    }
                    if(i>tempY && i<(tempLimit-1-tempY)){
                        val h=tempRawData(i)(tempX)
                        val n=tempRawData(i)(tempLimit-1-tempY)
                        getList+=n
                        headList+=h
                    }
                }
            }
            makeLoop
            val gTemp:List[Int] = getList.toList
            val hTemp:List[Int] = headList.toList.reverse
            print( gTemp:::hTemp mkString ",")
            print(",")
            val tempX1=tempX+1
            val tempY1=tempY+1
            if(tempX1<(tempLimit+1)/2) loopList(tempRawData,tempX1,tempY1,tempLimit)
        }
        loopList(rawList,x,y,limit)
        println("\n")
    }
}
