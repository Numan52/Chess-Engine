function formatTime(seconds) {
    let minutes = Math.floor(seconds / 60)
    let remainingSeconds = seconds % 60
    
    minutes = minutes < 10 ? "0" + minutes : minutes
    remainingSeconds = remainingSeconds < 10 ? "0" + remainingSeconds : remainingSeconds
    return `${minutes}:${remainingSeconds}`
}


export {formatTime}