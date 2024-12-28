function handleGameStart(containerElement) {
    const gameStartContainer = document.createElement("div")
    gameStartContainer.className = "game-start-container"
    
    const text = document.createElement("div")
    text.textContent = "I play as"

    const playAsSideDiv = document.createElement("div")
    whiteSide.classList("play-as-side-container")

    const playAsWhite = document.createElement("img")
    playAsWhite.src = "public/img/chesspieces/wikipedia/wK"

    const playAsBlack = document.createElement("img")
    playAsBlack.src = "public/img/chesspieces/wikipedia/bK"
    
    const playAsRandom = document.createElement("img")
    playAsRandom.src = ""

    gameStartContainer.append(text)
    gameStartContainer.append(playAsSideDiv)
    playAsSideDiv.append(playAsWhite)
    playAsSideDiv.append(playAsBlack)
    playAsSideDiv.append(playAsRandom)
    containerElement.append(gameStartContainer)

    timeSelection(containerElement)

}


function timeSelection(containerElement) {
    const timeContainer = document.createElement("div")

    const fiveMin = document.createElement("button")
    const tenMin = document.createElement("button")
    const fifteenMin = document.createElement("button")

    fiveMin.textContent = "5 Minutes"
    tenMin.textContent = "10 Minutes"
    fifteenMin.textContent = "15 Minutes"

    timeContainer.append(fiveMin)
    timeContainer.append(tenMin)
    timeContainer.append(fifteenMin)

    containerElement.append(timeContainer)
}