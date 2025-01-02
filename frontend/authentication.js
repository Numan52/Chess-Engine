function isLoggedIn() {
    const token = localStorage.getItem("token")
    return token != null
}

function getUsernameFromToken(token) {
    const payload = JSON.parse(atob(token.split('.')[1]));  // Decode JWT token
    return payload.username;  // Return username or "Guest" if not found
  }

export {isLoggedIn, getUsernameFromToken}