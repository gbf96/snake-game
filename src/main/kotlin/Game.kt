import pt.isel.canvas.*
import kotlin.random.Random

//Representa o status do jogo, altera as posições do jogador de acordo com o tempo, adiciona
//blocos e a maçã em espaços livres, lida com a interface do jogo.

data class Game(val snake: Snake, val wall: List<Position>, val apple: Position?, val score: Int)

const val WIDTH = 20
const val HEIGHT = 16
const val CELLSIZE = 32
const val MARGIN = 5
const val  GRAY = 0x202020

//Desenho
fun Game.draw(canvas: Canvas) {
    canvas.erase()
    drawWalls(canvas)
    drawApple(canvas)
    drawSnake(canvas)
    drawUI(canvas)

    if (isGameOver()) {
        val message = if (this.snake.body.size >= 60) "YOU WIN" else "YOU LOSE"
        canvas.drawText(((WIDTH/2) * CELLSIZE) - (3*CELLSIZE), ((HEIGHT + 1) * CELLSIZE) - MARGIN, message, WHITE)
    }
}

fun Game.drawWalls(canvas: Canvas) {
    for (wallBlock in this.wall) {
        canvas.drawImage("bricks.png", wallBlock.x * CELLSIZE, wallBlock.y * CELLSIZE, CELLSIZE, CELLSIZE)
    }
}

fun Game.drawApple(canvas: Canvas) {
    if(this.apple != null)
        canvas.drawImage("snake.png|0,192,64,64", this.apple.x * CELLSIZE, this.apple.y * CELLSIZE, CELLSIZE, CELLSIZE)
}

fun Game.drawSnake(canvas: Canvas) {
    this.snake.draw(canvas)
}

fun Game.drawUI(canvas: Canvas) {
    canvas.drawRect(0, HEIGHT * CELLSIZE, WIDTH * CELLSIZE, CELLSIZE, GRAY)
    canvas.drawText(0, ((HEIGHT + 1) * CELLSIZE) - MARGIN, "score: ${this.score}", WHITE)
    canvas.drawText((WIDTH - MARGIN) * CELLSIZE, ((HEIGHT + 1) * CELLSIZE) - MARGIN, "size: ${this.snake.body.size}", WHITE)
}

//Altera direção da Snake
fun Game.up(): Game{
    return Game(this.snake.up(this),this.wall, this.apple, this.score)
}

fun Game.down(): Game{
    return Game(this.snake.down(this),this.wall, this.apple, this.score)
}

fun Game.left(): Game{
    return Game(this.snake.left(this),this.wall, this.apple, this.score)
}

fun Game.right(): Game{
    return Game(this.snake.right(this),this.wall, this.apple, this.score)
}

//Altera posição e aumenta o tamanho da Snake
fun Game.moveAndGrow(): Game{
    return Game(snake.moveAndGrow(),this.wall, this.apple,this.score)
}

//Verifica se a cobra comeu a maçã
fun Game.grow(): Game{
    return Game(snake.grow(this),this.wall, this.apple,this.score)
}

//Verifica se a Snake está parada, com um bloco ou corpo em sua frente.
fun Game.isStopped(): Game{
    return Game(snake.isStopped(this),this.wall, this.apple,this.score)
}

//Se a maçã for comida ou nula, cria uma nova, em posição livre, e adiciona um ponto ao score.
fun Game.updateApple(): Game{
    val score = if(apple == null) this.score else this.score+1
    if(this.snake.body.first() == this.apple || this.apple == null) {
        if(apple != null) playSound("apple")
        var apple: Position
        do {
            apple = Position(Random.nextInt(20), Random.nextInt(16))
        } while (apple in this.wall || apple in this.snake.body)

        return Game(this.snake, this.wall, apple, score)
    }
    return this
}

//Adiciona paredes
fun Game.addWall(): Game{
    var block: Position
    do {
        block = Position(Random.nextInt(20), Random.nextInt(16))
    }while(block in this.wall || block in this.snake.body || block == apple)

    val newWall = this.wall + block
    return Game(this.snake, newWall, this.apple, this.score)
}

//Verifica se a cobra não tem mais como se mover
fun Game.isGameOver(): Boolean{
    val occupied = this.wall + this.snake.body
    return this.snake.body.first().up() in occupied && this.snake.body.first().down() in occupied && this.snake.body.first().left() in occupied && this.snake.body.first().right() in occupied
}

fun main() {
    onStart{
        val canvas = Canvas(WIDTH * CELLSIZE,(HEIGHT + 1) * CELLSIZE, BLACK)
        val snake = Snake(listOf(Position(WIDTH/2, HEIGHT/2)), Direction.RIGHT, 4, false)
        val walls = listOf(
            Position(0, 0), Position(0, 1), Position(0, 2), Position(1, 0), Position(2, 0),
            Position(0, HEIGHT - 1), Position(0, HEIGHT - 2), Position(0, HEIGHT - 3), Position(1, HEIGHT - 1), Position(2, HEIGHT - 1),
            Position(WIDTH - 1, 2), Position(WIDTH - 1, 1), Position(WIDTH - 1, 0), Position(WIDTH - 2, 0), Position(WIDTH - 3, 0),
            Position(WIDTH - 1, HEIGHT - 1), Position(WIDTH - 2, HEIGHT - 1), Position(WIDTH - 3, HEIGHT - 1), Position(WIDTH - 1, HEIGHT - 2), Position(WIDTH - 1, HEIGHT - 3)
        )
        var game = Game(snake, walls,  null, 0)

        game.draw(canvas)
        canvas.onTimeProgress(250) {

            canvas.onKeyPressed { k ->
                game = when (k.code) {
                    UP_CODE -> game.up()
                    DOWN_CODE -> game.down()
                    LEFT_CODE -> game.left()
                    RIGHT_CODE -> game.right()
                    else -> game
                }
            }
            game = game.updateApple()
            game = game.isStopped()
            game = game.grow()
            game = game.moveAndGrow()
            game.draw(canvas)
        }

        canvas.onTimeProgress(5000) {
            game = game.addWall()
        }
    }
}