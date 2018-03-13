import genomeTools

object toolsEntryPoint{
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
            case 'genomeStat' => Console.err.println(genomeStatHelp)
            case _            => Console.err.println(othercommand)
        }
    }
    def main(args:List[String])={
        args match {
            case 'genomeStat'::Nil  => showHelp('genomeStat')
            case 'genomeStat'::argv => {
                                            val excute=new calFaN50(argv.head)
                                            println(excute.faName)
                                            excute.calculateN()
                                        }
            case _                  => showHelp('other')
        }
    }
}



