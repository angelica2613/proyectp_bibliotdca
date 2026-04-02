// form-validation.js
// JavaScript for form validation in nuevo-*.html and editar-*.html templates
document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            let valid = true;
            const inputs = form.querySelectorAll('input[required], select[required]');
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    valid = false;
                    input.classList.add('border-red-500');
                    const error = document.createElement('p');
                    error.className = 'text-red-500 text-sm mt-1';
                    error.textContent = 'Este campo es obligatorio';
                    if (!input.nextElementSibling?.classList.contains('text-red-500')) {
                        input.parentNode.appendChild(error);
                    }
                } else {
                    input.classList.remove('border-red-500');
                    const error = input.nextElementSibling;
                    if (error?.classList.contains('text-red-500')) {
                        error.remove();
                    }
                }
            });

            // Email validation
            const emailInputs = form.querySelectorAll('input[type="email"]');
            emailInputs.forEach(email => {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (email.value && !emailRegex.test(email.value)) {
                    valid = false;
                    email.classList.add('border-red-500');
                    const error = document.createElement('p');
                    error.className = 'text-red-500 text-sm mt-1';
                    error.textContent = 'Por favor, ingrese un correo válido';
                    if (!email.nextElementSibling?.classList.contains('text-red-500')) {
                        email.parentNode.appendChild(error);
                    }
                } else {
                    email.classList.remove('border-red-500');
                    const error = email.nextElementSibling;
                    if (error?.classList.contains('text-red-500')) {
                        error.remove();
                    }
                }
            });

            // Additional validation for nuevo-libro.html (image URL)
            if (form.action.includes('/libros')) {
                const imagenInput = form.querySelector('input[name="imagen"]');
                if (imagenInput?.value) {
                    const urlRegex = /^(https?:\/\/.*\.(?:png|jpg|jpeg|gif))$/i;
                    if (!urlRegex.test(imagenInput.value)) {
                        valid = false;
                        imagenInput.classList.add('border-red-500');
                        const error = document.createElement('p');
                        error.className = 'text-red-500 text-sm mt-1';
                        error.textContent = 'Por favor, ingrese una URL de imagen válida (png, jpg, jpeg, gif)';
                        if (!imagenInput.nextElementSibling?.classList.contains('text-red-500')) {
                            imagenInput.parentNode.appendChild(error);
                        }
                    }
                }
            }

            if (!valid) {
                e.preventDefault();
                showMessage('Por favor, corrija los errores en el formulario.', 'error');
            }
        });
    });
});

// Utility to show messages
function showMessage(message, type = 'success') {
    const msgDiv = document.createElement('div');
    msgDiv.className = `fixed top-4 right-4 p-4 rounded shadow-lg text-white ${type === 'success' ? 'bg-green-500' : 'bg-red-500'}`;
    msgDiv.textContent = message;
    document.body.appendChild(msgDiv);
    setTimeout(() => msgDiv.remove(), 3000);
}
window.showMessage = showMessage;