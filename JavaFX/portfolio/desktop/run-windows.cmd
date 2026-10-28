@echo off
REM Install JDK 21 and Maven, start the backend first.
REM Optional: set PORTFOLIO_API_URL=https://your-portfolio.example.com
mvn clean javafx:run
pause
