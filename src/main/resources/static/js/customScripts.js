function logout() {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/auth/logout';

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
}

function uploadPhoto(){
    document.getElementById('profile-form').submit();
}

function getRandomPosition() {
    const randomX = Math.floor(Math.random() * 100);
    const randomY = Math.floor(Math.random() * 100);
    return `${randomX}% ${randomY}%`;
}

function setRandomBackgroundPosition() {
    const element = document.getElementById('background-images');
    element.style.backgroundPosition = getRandomPosition();
}

setRandomBackgroundPosition();

// Optionally, set random position every few seconds
setInterval(setRandomBackgroundPosition, 10000); // 10 seconds
