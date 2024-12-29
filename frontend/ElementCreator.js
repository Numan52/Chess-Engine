function createButton(selector, onClick, text) {
    const button = document.createElement("button")
    button.className = selector
    button.textContent = text
    button.addEventListener("click", onClick)

    return button
}


function createImage(selector, src, onClick) {
    const img = document.createElement("img")
    img.classname = selector
    img.src = src
    img.addEventListener("click", onClick)
    
    return img
}

export {createButton, createImage}