import pt.isel.canvas.Canvas

// Representa o jogador e o desenha, consoante sua posição e orientação, e altera seus atributos.
enum class Direction {
    UP, DOWN, LEFT, RIGHT;
}

data class Snake(val body: List<Position>, val dir: Direction, val toGrow: Int, val stopped: Boolean)

fun Snake.draw(canvas: Canvas) {
    this.body.forEachIndexed { index, position ->
        val x = position.x * CELLSIZE
        val y = position.y * CELLSIZE
        val cauda = this.body.size - 1
        val partImage = when {
            index == 0 -> {
                when (this.dir) {
                    Direction.UP -> "snake.png|192,0,64,64"
                    Direction.DOWN -> "snake.png|256,64,64,64"
                    Direction.RIGHT -> "snake.png|256,0,64,64"
                    Direction.LEFT -> "snake.png|192,64,64,64"
                }
            }
            index == cauda -> {
                val previous = body[cauda - 1]
                when {
                    previous == body[cauda].up() -> "snake.png|192,128,64,64"
                    previous == body[cauda].down() -> "snake.png|256,192,64,64"
                    previous == body[cauda].left() -> "snake.png|192,192,64,64"
                    previous == body[cauda].right() -> "snake.png|256,128,64,64"
                    else -> "snake.png|0,0,64,64"
                }
            }
            else -> {
                val previous = body[index - 1]
                val next = body[index + 1]
                when {
                    // Reto vertical
                    (previous == body[index].up() && next == body[index].down()) ||
                            (previous == body[index].down() && next == body[index].up()) -> "snake.png|128,64,64,64"

                    // Reto horizontal
                    (previous == body[index].left() && next == body[index].right()) ||
                            (previous == body[index].right() && next == body[index].left()) -> "snake.png|64,0,64,64"

                    // Curva superior esquerda
                    (previous == body[index].up() && next == body[index].left()) ||
                            (previous == body[index].left() && next == body[index].up()) -> "snake.png|128,128,64,64"

                    // Curva superior direita
                    (previous == body[index].up() && next == body[index].right()) ||
                            (previous == body[index].right() && next == body[index].up()) -> "snake.png|0,64,64,64"

                    // Curva inferior esquerda
                    (previous == body[index].down() && next == body[index].left()) ||
                            (previous == body[index].left() && next == body[index].down()) -> "snake.png|128,0,64,64"

                    // Curva inferior direita
                    (previous == body[index].down() && next == body[index].right()) ||
                            (previous == body[index].right() && next == body[index].down()) -> "snake.png|0,0,64,64"

                    else -> "snake.png|0,0,64,64"
                }
            }
        }
        canvas.drawImage(partImage, x, y, CELLSIZE, CELLSIZE)
    }
}


fun Snake.up(game: Game):  Snake{
    if(this.body.first().up() in game.wall || this.body.first().up() in this.body )
        return this

    if(this.dir != Direction.DOWN)
        return Snake(this.body, Direction.UP, this.toGrow, this.stopped)

    return this
}

fun Snake.down(game: Game): Snake {
    if(this.body.first().down() in game.wall || this.body.first().down() in this.body)
        return this

    if(this.dir != Direction.UP)
        return Snake(this.body, Direction.DOWN, this.toGrow, this.stopped)

    return this
}

fun Snake.left(game: Game): Snake {
    if(this.body.first().left() in game.wall || this.body.first().left() in this.body)
        return this

    if(this.dir != Direction.RIGHT)
        return Snake(this.body, Direction.LEFT, this.toGrow, this.stopped)

    return this
}

fun Snake.right(game: Game): Snake {
    if(this.body.first().right() in game.wall || this.body.first().right() in this.body)
        return this

    if(this.dir != Direction.LEFT)
        return Snake(this.body,Direction.RIGHT, this.toGrow, this.stopped)

    return this
}

fun Snake.isStopped(game: Game): Snake {
    val nextPosition = when (this.dir) {
        Direction.UP -> this.body.first().up()
        Direction.DOWN -> this.body.first().down()
        Direction.LEFT -> this.body.first().left()
        Direction.RIGHT -> this.body.first().right()
    }
    if(nextPosition in game.wall || nextPosition in this.body)
        return Snake(this.body, this.dir, this.toGrow, true)
    else{
        return Snake(this.body, this.dir, this.toGrow, false)
    }
}


fun Snake.move(): Position {
    if (this.stopped) return this.body.first()

    when (this.dir) {
        Direction.UP -> return this.body.first().up()
        Direction.DOWN -> return this.body.first().down()
        Direction.LEFT -> return this.body.first().left()
        Direction.RIGHT -> return this.body.first().right()
    }
}

fun Snake.grow(game: Game): Snake {
    if(game.snake.body.first().next(game) == game.apple)
        return Snake(this.body, this.dir, this.toGrow + 5, this.stopped)

    return this
}

fun Snake.moveAndGrow(): Snake {
    if (this.stopped) return this

    val newHead = this.move()
    val newBody = mutableListOf(newHead)

    if(toGrow == 0 && !this.stopped) {
        newBody.addAll(this.body.dropLast(1))
        return Snake(newBody, this.dir, this.toGrow, this.stopped)
    }else{
        newBody.addAll(this.body)
        return Snake(newBody, this.dir, (this.toGrow-1), this.stopped)
    }
}



