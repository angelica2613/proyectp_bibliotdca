// dashboard.js
// JavaScript for dashboard.html to handle dashboard interactions
document.addEventListener('DOMContentLoaded', () => {
    // Show welcome message
    const userSpan = document.querySelector('.navbar span');
    if (userSpan) {
        const welcomeMessage = userSpan.textContent;
        showMessage(`¡${welcomeMessage}!`, 'success');
    }
});

// Utility to show success/error messages
function showMessage(message, type = 'success') {
    const msgDiv = document.createElement('div');
    msgDiv.className = `fixed top-4 right-4 p-4 rounded shadow-lg text-white ${type === 'success' ? 'bg-green-500' : 'bg-red-500'}`;
    msgDiv.textContent = message;
    document.body.appendChild(msgDiv);
    setTimeout(() => msgDiv.remove(), 3000);
}
window.showMessage = showMessage;