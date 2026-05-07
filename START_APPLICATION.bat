@echo off
REM =============================================================================
REM RankUp E-Sports Platform - Application Launcher
REM =============================================================================

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║           🚀 RankUp E-Sports Platform Launcher 🚀              ║
echo ║                                                                ║
echo ║                 Your Application is Ready!                    ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Check if in correct directory
if not exist "pom.xml" (
    echo ❌ ERROR: pom.xml not found!
    echo Please run this script from the project root directory.
    pause
    exit /b 1
)

echo ✅ Project directory verified
echo.

REM Display menu
:menu
echo.
echo ┌─────────────────────────────────────────────────────────────────┐
echo │                        SELECT AN OPTION                         │
echo ├─────────────────────────────────────────────────────────────────┤
echo │                                                                 │
echo │  1) 🚀 Run Application (mvn javafx:run)                         │
echo │  2) 🧹 Clean Build (mvn clean)                                  │
echo │  3) 🔨 Compile Project (mvn compile)                            │
echo │  4) 📦 Package Project (mvn package)                            │
echo │  5) 🧪 Run Tests (mvn test)                                     │
echo │  6) 🔄 Clean and Run (mvn clean javafx:run)                    │
echo │  7) 📖 Show Documentation Files                                 │
echo │  8) 📋 Show Help                                                │
echo │  9) ❌ Exit                                                     │
echo │                                                                 │
echo └─────────────────────────────────────────────────────────────────┘
echo.

set /p choice="Enter your choice (1-9): "

if "%choice%"=="1" goto run_app
if "%choice%"=="2" goto clean
if "%choice%"=="3" goto compile
if "%choice%"=="4" goto package
if "%choice%"=="5" goto test
if "%choice%"=="6" goto clean_run
if "%choice%"=="7" goto docs
if "%choice%"=="8" goto help
if "%choice%"=="9" goto exit_app

echo.
echo ❌ Invalid choice. Please try again.
timeout /t 2
cls
goto menu

:run_app
echo.
echo 🚀 Launching RankUp Application...
echo.
echo ┌─────────────────────────────────────────────────────────────────┐
echo │  CONSOLE OUTPUT - Watch for any errors below:                   │
echo │  ✅ "Database connection successful" = Good                    │
echo │  ✅ "Login screen ready" = Good                                │
echo │  ✅ "Application running" = Good                               │
echo │  ❌ Any "SQLException" = Database issue                         │
echo │  ❌ Any "NullPointerException" = Code issue                     │
echo └─────────────────────────────────────────────────────────────────┘
echo.

mvn javafx:run
goto menu

:clean
echo.
echo 🧹 Cleaning project...
mvn clean
echo.
echo ✅ Clean complete!
pause
goto menu

:compile
echo.
echo 🔨 Compiling project...
mvn compile
echo.
echo ✅ Compilation complete!
pause
goto menu

:package
echo.
echo 📦 Packaging project...
mvn package
echo.
echo ✅ Packaging complete!
pause
goto menu

:test
echo.
echo 🧪 Running tests...
mvn test
echo.
echo ✅ Tests complete!
pause
goto menu

:clean_run
echo.
echo 🔄 Clean build and launching application...
echo.
mvn clean javafx:run
goto menu

:docs
echo.
echo 📖 Documentation files in this project:
echo.
echo Core Features:
echo   • REMEMBER_ME_IMPLEMENTATION.md - Login Remember Me feature
echo   • CHATBOT_INTEGRATION_GUIDE.md - Chatbot setup and testing
echo   • LOGIN_ENHANCEMENTS.md - 20+ enhancement ideas
echo   • LOGIN_UI_IMPROVEMENTS.md - 10 quick improvements
echo   • CONSOLE_TESTING_GUIDE.md - Manual testing procedures
echo.
echo Complete Guides:
echo   • IMPLEMENTATION_SUMMARY.md - Project overview
echo   • COMPLETE_SYSTEM_LAUNCH_GUIDE.md - Full system testing
echo   • QUICK_IMPLEMENTATION_REFERENCE.md - Quick reference
echo.
echo All files are in: C:\Users\ghass\OneDrive\Desktop\project_java\
echo.
pause
goto menu

:help
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║                   RankUp Launch Helper                         ║
echo ╠════════════════════════════════════════════════════════════════╣
echo │                                                                ║
echo │ FEATURES IMPLEMENTED:                                         ║
echo │ ✅ Remember Me Checkbox (Login)                               ║
echo │ ✅ Secure Credential Storage                                  ║
echo │ ✅ Auto-fill on Restart                                       ║
echo │ ✅ Chatbot for New Players                                    ║
echo │ ✅ 30+ Chatbot Responses                                      ║
echo │ ✅ Dashboard Integration Ready                                ║
echo │                                                                ║
echo │ QUICK START:                                                   ║
echo │ 1. Run: mvn javafx:run                                         ║
echo │ 2. Login with your email/password                              ║
echo │ 3. Check "Remember me" to save credentials                    ║
echo │ 4. Click on Chatbot button to see 🤖 Chat                     ║
echo │ 5. Type "hello" to test chatbot                               ║
echo │                                                                ║
echo │ TESTING:                                                       ║
echo │ • Follow CONSOLE_TESTING_GUIDE.md for detailed steps          ║
echo │ • Check console for "Database connection successful"          ║
echo │ • Verify "Dashboard loaded" message appears                   ║
echo │ • Test Remember Me by restarting app                          ║
echo │ • Test Chatbot with suggestion buttons                        ║
echo │                                                                ║
echo │ TROUBLESHOOTING:                                               ║
echo │ • Database error? Check MySQL is running                      ║
echo │ • User not found? Create account via Sign Up                  ║
echo │ • Chatbot not loading? Check chatbot.fxml exists              ║
echo │ • Remember Me not working? Clear Java Preferences             ║
echo │                                                                ║
echo │ DOCUMENTATION:                                                 ║
echo │ • Read COMPLETE_SYSTEM_LAUNCH_GUIDE.md for full guide         ║
echo │ • Check CHATBOT_INTEGRATION_GUIDE.md for chatbot setup        ║
echo │ • See LOGIN_ENHANCEMENTS.md for future features               ║
echo │                                                                ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
pause
goto menu

:exit_app
echo.
echo ✅ Goodbye! Thank you for using RankUp!
echo.
exit /b 0

