object listSpiral{
    def main{
        val rawList:List[List[Int]]=list(list(1,2,3,4),list(5,6,7,8),list(9,10,11,12),list(13,14,15,16))
        val x:Int=0
        val y:Int=0
        val limit:Int=4
        def loopList(tempRawData:List[List[Int]],tempX:Int,tempY:Int,tempLimit:Int):List[Int]={
            val getList:List[Int] = for(i <- 0 to tempLimit){
                    if(i>=tempY){
                        val m:List[Int]=List.range(0,tempLimit)
                        val t:List[Int]
                        if(i>=(tempLimit+1)/2) t=m.reverse
                        else t=m
                        for(j <- t){
                            if(j>=tempX && j<=(tempLimit-1-tempX)){
                                val n=tempRawData(i)(j)
                            }
                        }
                    }
                } yield n
            print getList mkString ","
            val tempX1=tempX+1
            val tempY1=tempY+1
            if(tempX1<=(tempLimit+1)/2) loopList(tempRawData,tempX1,tempY1,tempLimit)
        }
        loopList(rawList,x,y,limit)
    }
}
