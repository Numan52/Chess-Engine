import { createButton, createImage } from "./ElementCreator"
import { formatTime } from "./utils"

function addGameStartUi(sideContainer, minutesContainer, setPlayerCallback, startGameCallback, setTimeCallback) {
    const container = document.querySelector(".game-start-container") 
    addSideSelection(sideContainer, setPlayerCallback)
    addTimeSelection(minutesContainer, setTimeCallback)

    const startBtn = document.createElement("button")
    startBtn.className = "start-game-button"
    startBtn.textContent = "Play"
    startBtn.addEventListener("click", startGameCallback)

    container.append(startBtn)
}


function addSideSelection(containerElement, setPlayerCallback) {
    const text = document.createElement("div")
    text.textContent = "I play as"
    text.className = "play-as-text"

    const playAsSideDiv = document.createElement("div")
    playAsSideDiv.className= "play-as-side-container"

    const sideChoicesContainer = document.createElement("div")
    sideChoicesContainer.className = "side-choices-container"

    const playAsWhite = createImage("play-as-white-img", "/img/chesspieces/wikipedia/wK.png", () => {
        setPlayerCallback("w")
        updateSelection(playAsSideDiv, "img", playAsWhite)
    })
    // Default
    setPlayerCallback("w")
    playAsWhite.classList.add("selected")
    
    const playAsBlack = createImage("play-as-black-img", "/img/chesspieces/wikipedia/bK.png", () => {
        setPlayerCallback("b")
        updateSelection(playAsSideDiv, "img", playAsBlack)
    })
    const playAsRandom = createImage("play-as-random-img", "/img/dice_2.png", () => {
        setPlayerCallback("r")
        updateSelection(playAsSideDiv, "img", playAsRandom)
    })
    playAsRandom.classList.add("random-side-img")


    playAsSideDiv.append(text)
    sideChoicesContainer.append(playAsWhite)
    sideChoicesContainer.append(playAsBlack)
    sideChoicesContainer.append(playAsRandom)
    playAsSideDiv.append(sideChoicesContainer)
    
    
    containerElement.append(playAsSideDiv)

    
}


function updateSelection(container, elementType, selectedElement) {
    container.querySelectorAll(elementType).forEach((child) => child.classList.remove("selected"))

    selectedElement.classList.add("selected")
}


function addTimeSelection(containerElement, setTimeCallback) {
    const timeContainer = document.createElement("div")
    timeContainer.className = "timer-container"

    const fiveMin = createButton("fiveMinButton", () => {
        setTimeCallback(5)
        updateSelection(timeContainer, "button", fiveMin)
    }, "5 minutes")
    
    const tenMin = createButton("tenMinButton", () => {
        setTimeCallback(10)
        updateSelection(timeContainer, "button", tenMin)
    }, "10 minutes")
    // Default
    setTimeCallback(10)
    tenMin.classList.add("selected")

    const fifteenMin = createButton("fifteenMinButton", () => {
        setTimeCallback(15)
        updateSelection(timeContainer, "button", fifteenMin)
    }, "15 minutes")
    


    timeContainer.append(fiveMin)
    timeContainer.append(tenMin)
    timeContainer.append(fifteenMin)

    containerElement.append(timeContainer)
}


function intializeTimeControl(timeLimit) {
    const oppRemainingTime = document.querySelector(".opp-remaining-time")
    const ownRemainingTime = document.querySelector(".own-remaining-time")
    const status = document.querySelector(".status-message")
    
    oppRemainingTime.textContent = formatTime(timeLimit)
    ownRemainingTime.textContent = formatTime(timeLimit)
    
    status.textContent = "White to move"

}


function initializeSurrenderButton(button, isStartingPosition,  onClick) {
    button.textContent = isStartingPosition ? "Cancel Game" : "Surrender"
    button.className = "surrender-btn"
    button.classList.remove("hidden")
    button.addEventListener("click", onClick)
}



export {addGameStartUi, addTimeSelection, intializeTimeControl, initializeSurrenderButton}