@REG QUERY "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall" | findstr Docker > nul
@if "%errorlevel%" == "0" (
    docker-compose up -d
) else (
    @echo "Not detected Docker Desktop!"
)
@pause