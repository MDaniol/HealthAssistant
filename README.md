### Short Summary

Prototype of the mobile app and backend which aims to:
- log in user with given credentials (v)
- check if there are any pending consents for clinical study (v)
- allow user to accept consent (v)
- after consent acceptance user is directed to file selection screen (v)
- on file selection screen user might want to select the files to upload (x)
- after file is uploaded all other user devices are synced (x)

### Design Decisions
- App build for Android in Kotlin with possible extension to the multiplatform component (libraries selected with multiplatformization in mind) - time constraints and KMP compilation to the native code (ObjC)

### App Architecture
App architecture has limited complexity and clearance due to the time constraints
- App designed in SOLID principles in mind
- Each user-story has it's own package
- `core` package for shared code 
- JWT tokens handled by Ktor library with token store integrated
- Business logic handled by ViewModels + use cases
- Navigation handled by NavHost
###
