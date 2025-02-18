//Representa uma posição XY, altera posições do jogador.

data class Position(val x: Int, val y: Int)

fun Position.up(): Position {
    if(y < 1) return Position(x,HEIGHT-1)

    return Position(x,y-1)
}

fun Position.down(): Position {
    if(y >= (HEIGHT-1)) return Position(x,0)

    return Position(x,y+1)
}

fun Position.left(): Position {
    if(x < 1) return Position(WIDTH-1,y)

    return Position(x-1, y)
}

fun Position.right(): Position {
    if(x >= (WIDTH-1)) return Position(0,y)

    return Position(x+1, y)
}

fun Position.next(game: Game): Position {
    return when (game.snake.dir) {
        Direction.UP -> {
            if (this.up() in game.wall) return this
            this.up()
        }
        Direction.DOWN -> {
            if (this.down() in game.wall) return this
            this.down()
        }
        Direction.LEFT -> {
            if (this.left() in game.wall) return this
            this.left()
        }
        Direction.RIGHT -> {
            if (this.right() in game.wall) return this
            this.right()
        }
    }
}
