function renderFooter() {
  const host = document.getElementById("footer"); if (!host) return;
  const footer = document.createElement("footer"); footer.className = "footer";
  const brand = document.createElement("strong"); brand.textContent = "SmartCare";
  const text = document.createElement("span"); text.textContent = "Clinic management · Educational demonstration";
  const link = document.createElement("a"); link.href = "/"; link.textContent = "Choose your role";
  footer.append(brand, text, link); host.replaceChildren(footer);
}
renderFooter();
