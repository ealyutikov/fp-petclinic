import sbtwelcome._

logo :=
  """ ____     ____              ____     ____       ______    ____       __         ______      __  __      ______      ____
    |/\  _`\  /\  _`\           /\  _`\  /\  _`\    /\__  _\  /\  _`\    /\ \       /\__  _\    /\ \/\ \    /\__  _\    /\  _`\
    |\ \ \L\_\\ \ \L\ \         \ \ \L\ \\ \ \L\_\  \/_/\ \/  \ \ \/\_\  \ \ \      \/_/\ \/    \ \ `\\ \   \/_/\ \/    \ \ \/\_\
    | \ \  _\/ \ \ ,__/ _______  \ \ ,__/ \ \  _\L     \ \ \   \ \ \/_/_  \ \ \  __    \ \ \     \ \ , ` \     \ \ \     \ \ \/_/_
    |  \ \ \/   \ \ \/ /\______\  \ \ \/   \ \ \L\ \    \ \ \   \ \ \L\ \  \ \ \L\ \    \_\ \__   \ \ \`\ \     \_\ \__   \ \ \L\ \
    |   \ \_\    \ \_\ \/______/   \ \_\    \ \____/     \ \_\   \ \____/   \ \____/    /\_____\   \ \_\ \_\    /\_____\   \ \____/
    |    \/_/     \/_/              \/_/     \/___/       \/_/    \/___/     \/___/     \/_____/    \/_/\/_/    \/_____/    \/___/""".stripMargin

usefulTasks := Seq(
  UsefulTask("a", "compile ", "compile all modules with file-watch enabled"),
  UsefulTask("b", "prepare ", "scalafmt & scalafix & compile"),
  UsefulTask("c", "check   ", "scalafmt check & scalafix check"),
  UsefulTask("e", "fmtCheck", "check format code"),
  UsefulTask("d", "fmt     ", "format code")
)
