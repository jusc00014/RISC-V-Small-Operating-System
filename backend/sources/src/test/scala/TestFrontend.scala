import models._

abstract class TestFrontend {
    def getKey(): (Int, Char)
    def getAction(): UIAction
    def publishCurrentState(state: State): Unit
    def publishInitialState(initialState: InitialState): Unit
    def publishUpdate(update: StateUpdate): Unit
    def getConfig(): Config
    def publishError(error: String): Unit
}
