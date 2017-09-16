
import effect.Err
import effect.Val
import game.Game


/**
 * Main
 */
fun main(args : Array<String>)
{
    System.out.println("It's working")
    if (args.isNotEmpty())
    {
        System.out.print("Arguments")
        val gameDocFilepath = args[0]
        val game = loadGame(gameDocFilepath)
        if (game != null)
            System.out.print("Game parsed")
    }
    else
    {
        System.out.print("Specify game definition file path.")
    }

}



private fun loadGame(gameDocFilePath : String) : Game?
{
    val game = Tome.loadGame(gameDocFilePath)
    return when (game)
    {
        is Val -> game.value
        is Err -> {
            System.out.print(game.error.message())
            null
        }
    }
}
