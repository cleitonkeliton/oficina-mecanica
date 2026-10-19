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

    // Botão "Gerar senha": preenche o campo indicado com 6 números aleatórios
    document.querySelectorAll('[data-gerar-senha]').forEach(function (botao) {
        botao.addEventListener('click', function () {
            var campo = document.getElementById(botao.getAttribute('data-gerar-senha'));
            if (!campo) return;
            var numeros = new Uint32Array(6);
            window.crypto.getRandomValues(numeros);
            var senha = Array.prototype.map.call(numeros, function (n) { return n % 10; }).join('');
            campo.value = senha;
            campo.type = 'text';
            campo.focus();
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
