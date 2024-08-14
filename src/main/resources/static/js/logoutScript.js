function logout() {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/auth/logout';  // Make sure this matches your logout URL

    // CSRF token if required
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
}
