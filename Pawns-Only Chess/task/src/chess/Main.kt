package chess

import kotlin.math.abs

var w = mutableListOf("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2")
var b = mutableListOf("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7")
var cur = mutableListOf(Pair(b, ""), Pair(w, ""))
var jump = "  "
var win = false

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:").also { cur[1] = Pair(w, readLine()!!) }
    println("Second Player's name:").also { cur[0] = Pair(b, readLine()!!) }
    printChessBoard()
    while (!win) {
        println("${cur.first().second}'s turn:")
        val input = readLine()!!
        when {
            input.matches(Regex("([a-h][1-8]){2}")) -> input.chunked(2).run { checkMove(first(), last()) }
            input == "exit" -> print("Bye!").also { return }
            else -> println("Invalid Input")
        }
    }
    print("Bye!")
}

fun printChessBoard() {
    cur.reverse()
    repeat(8) { y ->
        print("  +" + "---+".repeat(8) + "\n${8 - y} |")
        for (x in 'a'..'h') " ${printPawn("$x${8 - y}")} |".run(::print)
        println()
    }
    println("  +" + "---+".repeat(8) + "\n    a   b   c   d   e   f   g   h")
}

fun printPawn(s: String) = when {checkPawn(s, b) -> "B"; checkPawn(s, w) -> "W"; else -> " " }

fun checkPawn(coords: String, coord: MutableList<String>) = coord.contains(coords)

fun updateBoard(cl: MutableList<String>, first: String, last: String) {
    cl.replaceAll { if (it == first) last else it }.also { printChessBoard() }
    checkForWinOrDraw()
}

fun checkMove(first: String, last: String, cl: MutableList<String> = cur[0].first, ol: MutableList<String> = cur[1].first) {
    val (pcx, pcy, pnx, pny) = listOf(first.toList(), last.toList()).flatten()
    val t = if(cl == w) 1 else -1 ; val step = (pny - pcy) * t

    if (!checkPawn(first, cl)) { println("No ${if (cl == w) "white" else "black"} pawn at $first").also { return } }

    when(abs(pcx - pnx)) {
        0 -> if ((pcy in "27" && step in 1..2) || step == 1 && !checkPawn(last, ol)) {
            jump = if (step == 2) last else "  "
            updateBoard(cl, first, last).also { return }
        }
        1 -> {
            if (checkPawn("${pcx + t}${pcy + t}", ol) || checkPawn("${pcx - t}${pcy + t}", ol)) {
                ol.remove(last)
                updateBoard(cl, first, last).also { return }
            }
            if ((pcx - jump[0]) == (pny - pcy)) {
                ol.remove(jump)
                updateBoard(cl, first, last).also { return }
            }
        }
    }
    println("Invalid Input")
}

fun checkForWinOrDraw() {
    val current = cur.last().first == w
    win = when {
        Regex(".[18]").containsMatchIn(cur.last().first.toString()) -> {
        println("${if (current) "White" else "Black"} Wins!")
            true
        }
        cur.first().first.isEmpty() -> {
            println("${if (current) "White" else "Black"} Wins!")
            true
        }
        checkCanEat(current) -> {
            println("Stalemate!")
            true
        }
        else -> false
    }
}

fun checkCanEat(current: Boolean): Boolean {
    val specA =  cur.last().first.map { "${it.first()}${it.last() + if (current) 1 else -1}" }
    val specBA = cur.first().first.map { "${it.first() + 1}${it.last()}" }
    val specBB = cur.first().first.map { "${it.first() - 1}${it.last()}" }
    return specA.containsAll(cur.first().first) &&
            specA.intersect(specBA.toSet()).isEmpty() &&
            specA.intersect(specBB.toSet()).isEmpty()
}