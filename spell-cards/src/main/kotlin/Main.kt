
import Tome.loadGame
import effect.Err
import effect.Val
import game.Game
import game.engine.value.Value
import game.engine.value.ValueSet
import game.engine.value.ValueSetId
import game.engine.value.ValueText
import game.engine.variable.VariableId
import game.engine.variable.VariableName
import org.jfree.graphics2d.svg.SVGGraphics2D
import java.awt.Color
import java.awt.Rectangle
import java.io.File



// ---------------------------------------------------------------------------------------------
// MAIN
// ---------------------------------------------------------------------------------------------

/**
 * Main
 */
fun main(args : Array<String>)
{
    if (args.size == 2)
    {
        val gameSchemaFilepath = args[0]
        val gameDocFilepath    = args[1]

        val gameSpells = parseGameSpells(gameSchemaFilepath, gameDocFilepath)

        generateCardSVGFiles(gameSpells)
    }
    else
    {
        System.out.print("Specify game schema and document path.")
    }
}


// ---------------------------------------------------------------------------------------------
// SPELLS
// ---------------------------------------------------------------------------------------------

data class Spell(val name : String,
                 val level : String,
                 val school : String,
                 val castingTime : String,
                 val range : String,
                 val components : String,
                 val duration : String,
                 val description : String)

// Get Spells From Game
// ---------------------------------------------------------------------------------------------

fun parseGameSpells(gameSchemaFilePath : String, gameDocFilePath : String) : List<Spell> =
    loadGame(gameSchemaFilePath, gameDocFilePath)?.let(::gameSpells) ?: listOf()


fun gameSpells(game : Game) : List<Spell> =
    game.engine.valueSetWithId(ValueSetId("spells"))?.let(::valueSetSpells) ?: listOf()


fun valueSetSpells(valueSet : ValueSet) : List<Spell> =
        valueSet.values().mapNotNull(::valueToSpell)


fun valueToSpell(value : Value) : Spell? = when (value)
{
    is ValueText ->
    {
        // Name
        val name        = value.value

        // Level
        var levelVariableId = VariableId(VariableName("level"))
        val level = value.variableWithId(levelVariableId)?.valueString() ?: "N/A"

        // School
        var schoolVariableId = VariableId(VariableName("school"))
        val school = value.variableWithId(schoolVariableId)?.valueString() ?: "N/A"

        // Casting Time
        var castingTimeVariableId = VariableId(VariableName("casting_time"))
        val castingTime = value.variableWithId(castingTimeVariableId)?.valueString() ?: "N/A"

        // Range
        var rangeVariableId = VariableId(VariableName("range"))
        val range = value.variableWithId(rangeVariableId)?.valueString() ?: "N/A"

        // Components
        var componentsVariableId = VariableId(VariableName("components"))
        val components = value.variableWithId(componentsVariableId)?.valueString() ?: "N/A"

        // Duration
        var durationVariableId = VariableId(VariableName("duration"))
        val duration = value.variableWithId(durationVariableId)?.valueString() ?: "N/A"

        // Description
        var descriptionVariableId = VariableId(VariableName("description"))
        val description = value.variableWithId(descriptionVariableId)?.valueString() ?: "N/A"

        Spell(name, level, school, castingTime, range, components, duration, description)
    }
    else         -> {
        System.out.println("Spell value is not a text value.")
        null
    }
}


// Load Game
// ---------------------------------------------------------------------------------------------

fun loadGame(gameSchemaFilePath : String, gameDocFilePath : String) : Game?
{
    Tome.setSchemasPath(gameSchemaFilePath)
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


fun generateCardSVGFiles(spells : List<Spell>)
{
    spells.forEach {
        val spellCardSVG = spellCardSVG(it)
        val fileName = it.name + ".svg"
        File(fileName).bufferedWriter().use { out ->
            out.write(spellCardSVG)
            System.out.println("Wrote file $fileName")
        }
    }
}


// ---------------------------------------------------------------------------------------------
// SVG
// ---------------------------------------------------------------------------------------------

fun spellCardSVG(spell : Spell) : String
{
    val g = SVGGraphics2D(300, 220)

    // Border
    g.paint = Color.RED
    g.draw(Rectangle(10, 10, 280, 180))

    // Name
    g.paint = Color.MAGENTA
    g.drawString(spell.name, 20, 20)

    return g.svgDocument
}
