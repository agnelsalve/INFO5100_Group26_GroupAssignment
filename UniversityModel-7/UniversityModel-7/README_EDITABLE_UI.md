
# Editable GUI Wrappers Added

This package `edu.univ.ui.form` contains NetBeans GUI Builder (`.form`) wrappers for your existing panels.
Open these files in NetBeans and use the **Design** tab:

- `MainWindow.form` (JFrame with tabs)
- `LoginForm.form` (wraps `edu.univ.ui.LoginPanel`)
- `AdminPanelForm.form` (wraps `edu.univ.ui.AdminPanel`)
- `FacultyPanelForm.form` (wraps `edu.univ.ui.FacultyPanel`)
- `RegistrarPanelForm.form` (wraps `edu.univ.ui.RegistrarPanel`)
- `StudentPanelForm.form` (wraps `edu.univ.ui.StudentPanel`)

### How it works
Each wrapper embeds the original panel as a custom component inside an inner panel.
You can add buttons/labels/etc. around it using the Design tab without altering your existing logic.

### Run
Set `edu.univ.ui.form.MainWindow` as the Main Class (Project Properties → Run), or run it directly.

