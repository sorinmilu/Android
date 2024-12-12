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
    emulator -avd <AVD_name>
    emulator -avd <AVD_name> -no-snapshot-save


Instalare    

    adb -s emulator-5554 install  app/build/outputs/apk/debug/app-debug.apk

    adb shell ps | findstr akrilki 

    adb uninstall ro.makore.akrilki_032