import genomeTools._

object toolsEntryPoint{
    def main(args:Array[String])={
        def showHelp(command:String):Unit = {
            val genomeStatHelp="""
            |java genometools.jar genomeStat <fa>
            |xxxxx@xxxx
            |20180313
            """
            val othercommand="""
            |unrecognize command; please use "genomeStat"
            """
            command match {
                case "genomeStat" => Console.err.println(genomeStatHelp)
                case _            => Console.err.println(othercommand)
            }
        }
        args match {
            case Array("genomeStat")            => showHelp("genomeStat")
            case Array("genomeStat",argv,_*)    => {
                                                    val excute=new calFaN50(argv)
                                                    println(excute.faName)
                                                    excute.calculateN()
                                                    }
            case _                              => showHelp("other")
        }
    }
}



