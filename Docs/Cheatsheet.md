<!-- TOC -->

- [Sdk tools](#sdk-tools)
- [Devices](#devices)
- [Build, clean](#build-clean)


<!-- /TOC -->

# Sdk tools

Lista tuturor pachetelor instalate

    sdkmanager --list_installed

Lista tuturor pachetelor disponivile

    sdkmanager --list

Identificarea unei imagini de sistem pentru un anumit API level

    sdkmanager --list | findstr android-35 [Windows]
    sdkmanager --list | grep android-35 [Linux]

Instalarea unei imagini de sistem

    sdkmanager "system-images;android-35;google_apis;x86_64"



# Devices

Crearea unui dispozitiv vbirtual

    avdmanager create avd -n '<AVD_name>' -k "system-images;android-34;google_apis;x86_64" -d pixel_7

Lista dispozitivelor virtuale existente

    avdmanager list avds

Lista sabloanelor pentru dispozitive virtuale (gen "Nexus, Pixel")

    avdmanager list devices

Stergere dispozitiv virtual

    avdmanager delete avd -n <AVD_name>

Pornire dispozitiv virtual

    emulator -list-avds
    emulator -avd <AVD_name> (pornește dispozitivul pe baza unui snapshot preexistent - dacă există)
    emulator -avd <AVD_name> -no-snapshot-save (pornește dispozitivul în cold boot)


Instalare    

    adb devices (listeaza toate dispozitivele pornite/conectate)

    adb -s emulator-5554 install  app/build/outputs/apk/debug/app-debug.apk


Dezinstalare

    aapt dump badging apk_name.apk | findstr package (putem afla numele pachetului daca nu il cunoastem)

    adb shell ps | findstr akrilki 

    adb uninstall ro.makore.akrilki_032


# Build, clean

In directorul aplicatiei 

    gradle build

    gradle clean


# ScrCpy


scrcpy --help afiseaza toate optiunile disponibile

scrcpy --max-size 1024 reduce dimensiunea ferestrei afisate

scrcpy --bit-rate 2M reduce latimea de banda video

scrcpy --record file.mp4 porneste inregistrarea ecranului

scrcpy --stay-awake mentine ecranul telefonului pornit

scrcpy --crop 1080:1920:0:0 afiseaza doar o portiune din ecranul telefonului
