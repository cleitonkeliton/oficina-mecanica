// Pequenos comportamentos das páginas (funciona sem bibliotecas externas)
document.addEventListener('DOMContentLoaded', function () {

    // Menu do site no celular
    document.querySelectorAll('[data-menu]').forEach(function (botao) {
        var alvo = document.getElementById(botao.getAttribute('data-menu'));
        if (!alvo) return;
        botao.addEventListener('click', function () {
            var aberto = alvo.classList.toggle('aberto');
            botao.setAttribute('aria-expanded', aberto ? 'true' : 'false');
        });
        alvo.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', function () {
                alvo.classList.remove('aberto');
                botao.setAttribute('aria-expanded', 'false');
            });
        });
    });

    // Pede confirmação antes de ações como excluir
    document.querySelectorAll('form[data-confirmar]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (!window.confirm(form.getAttribute('data-confirmar'))) {
                e.preventDefault();
            }
        });
    });

    // Botão "Gerar senha": 8 letras e números, sem os que se confundem (0/O, 1/l/I)
    var CARACTERES = 'abcdefghjkmnpqrstuvwxyz23456789';
    document.querySelectorAll('[data-gerar-senha]').forEach(function (botao) {
        botao.addEventListener('click', function () {
            var campo = document.getElementById(botao.getAttribute('data-gerar-senha'));
            if (!campo) return;
            var numeros = new Uint32Array(8);
            window.crypto.getRandomValues(numeros);
            var senha = Array.prototype.map.call(numeros, function (n) { return CARACTERES[n % CARACTERES.length]; }).join('');
            campo.value = senha;
            campo.type = 'text';
            campo.focus();
        });
    });

    // Evita enviar o mesmo formulário duas vezes (duplo clique)
    document.querySelectorAll('form[method=post]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (e.defaultPrevented) return;
            if (form.dataset.enviando) { e.preventDefault(); return; }
            form.dataset.enviando = '1';
            form.querySelectorAll('button[type=submit]').forEach(function (b) { b.setAttribute('aria-disabled', 'true'); });
        });
    });
    // Ao voltar pelo navegador, libera os formulários de novo
    window.addEventListener('pageshow', function () {
        document.querySelectorAll('form[data-enviando]').forEach(function (form) {
            delete form.dataset.enviando;
            form.querySelectorAll('button[aria-disabled]').forEach(function (b) { b.removeAttribute('aria-disabled'); });
        });
    });

    // No painel: o seletor de situação só envia quando a pessoa confirma no botão
    document.querySelectorAll('.status-rapido select').forEach(function (select) {
        var botao = select.form && select.form.querySelector('button');
        if (!botao) return;
        var original = select.value;
        botao.disabled = true;
        select.addEventListener('change', function () {
            botao.disabled = select.value === original;
        });
    });
});
