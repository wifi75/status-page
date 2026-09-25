// Pagina pubblica: tooltip delle barre, orologio, aggiornamento automatico.
(() => {
    const tip = document.getElementById("tip");
    if (tip) {
        document.addEventListener("mouseover", (e) => {
            const text = e.target.dataset?.tip;
            if (!text) {
                tip.style.opacity = 0;
                return;
            }
            tip.textContent = text;
            tip.style.opacity = 1;
        });
        document.addEventListener("mousemove", (e) => {
            tip.style.left = Math.min(e.clientX + 12, window.innerWidth - tip.offsetWidth - 8) + "px";
            tip.style.top = e.clientY - 34 + "px";
        });
    }

    const clock = document.getElementById("clock");
    const loadedAt = Date.now();
    if (clock) {
        setInterval(() => {
            const s = Math.round((Date.now() - loadedAt) / 1000);
            clock.textContent = s < 5 ? "aggiornato ora" : "aggiornato " + s + " s fa";
        }, 1000);
    }

    // ricarica ogni 60 s, solo se la scheda è visibile
    setInterval(() => {
        if (document.visibilityState === "visible") {
            location.reload();
        }
    }, 60000);
})();
