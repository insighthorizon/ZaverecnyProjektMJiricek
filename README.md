# Závěrečný projekt k rekvalifikačnímu kurzu Programátor WWW Aplikací Java (itnetwork)
    
# Autor: Marek Jiříček
17.10.2023

# Použité technologie
- Java, Spring Boot, Thymeleaf
    
# Toolchain
- Maven, IntelliJ (verze knihoven jsou v pom.xml)

# VLASTNÍ ZADÁNÍ
- Webová aplikace (dynamický web) pro správu nutriční databáze (evidence nutričních dat potravin). Potravina má název, kcal na 100 g hmotnosti, gramy bílkovin, sacharidů a tuků na 100 g hmotnosti.
- Umět procházet všechny potraviny (stránkování pro prezentaci databáze po částech), vyhledávat podle jména, přidávat potraviny, upravovat a mazat potraviny skrze webovou stránku.
- Entity jsou uloženy v (operační) paměti serveru. Není použita databáze. Data existují jen po dobu spuštění serveru.
- Pokus o dodržení konvencí MVC architektury. Proto jsou entity uloženy ve "virtuální databázi" a ne přímo součástí služby která by s databází komunikovala.

# Poznámky ke zvolenémů způsobu řešení
- Tuším, že v praxi se spousta věcí dělá jinak než jsem udělal. Například se asi používá Bean Validation API, Hybernate, a databázi v projektu vůbec nemám. Z časových důvodů jsem neabsolvoval bonusové materiály ke spring boot, kde jsou tyto věci nejspíš vysvětleny (ještě nemám ani splněný všechen povinný elearning a zbývá mi na to už jen týden)
- žádný javaScript, PUT, POST i DELETE request jsou zaločeny na html form
- Databáze tam není z důvodu času na projekt a kurz.
- Na vzhled jsem se nezaměřoval. Je tam CSS hlavně proto, aby se aplikace dala ladit.  
- Vycházel jsem ze znalostí poskytnutých v kurzu "Základy Spring Boot frameworku pro Javu"  https://www.itnetwork.cz/java/spring-boot/zaklady. Ale to by samozřejmě nestačilo, takže jsem si pomáhal googlením, ale jen ohledně maličkostí s Thymeleaf a pod.
- Celkově je návrh řešení čistě můj (například jak udělat stránkování, jak zakomponovat virtuální databázi do modelové vrstvy) a nesledoval jsem žádné celistvé návody.

