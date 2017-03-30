package ca.mcgill.science.ctf.api

/**
 * Created by Allan Wang on 29/03/2017.
 *
 * Data holder for ticket submissions
 * Keep in mind that newLines but have a \ prefix (\\n) for it to work properly with the form fill
 */
class TicketData {
    var name: String? = null
    val nameId = "edit-submitted-name"

    var studentNum: String? = null
    val studentNumId = "edit-submitted-mcgill-id"

    var email: String? = null
    val emailId = "edit-submitted-mcgill-email-address"

    var alternateEmail: String? = "taskforce@sus.mcgill.ca" //TODO verify email
    val alternateEmailId: String = "edit-submitted-alternate-email-address"

    var service: String? = "0" //val for "other"
    val serviceId = "edit-submitted-service"

    var problem: String? = null
    val problemId = "edit-submitted-problem-question"

    val problemHead = "To whom it may concern,\\n\\nThis is Computer Taskforce (CTF). "
    val problemTail = "\\n\\nThe printer is marked down right now. It will be great if the technician can inform us at our office, Burnside 1B19, or by email, after the issue is fixed.\\n\\nThank you!\\nComputer Taskforce (CTF)"

    var printer: PrinterId? = null

    enum class PrinterId(val serialNumber: String) {
        _1B16_North("XKP509632"),
        _1B16_South("XKP516518"),
        _1B17_North("XKP529682"),
        _1B17_South("XKP530648"),
        _1B18_North("XKP511152");

        //name matches the true printer name on TEPID
        fun getName(): String {
            return toString().substring(1).replace("_", "-")
        }
    }

    fun getInjector(): String {
        val builder = StringBuilder()
        builder.append(getFormVal(nameId, name))
        builder.append(getFormVal(studentNumId, studentNum))
        builder.append(getFormVal(emailId, email))
        builder.append(getFormVal(alternateEmailId, alternateEmail))
        builder.append(getFormVal(serviceId, service))
        builder.append(getFormVal(problemId, getFullProblem()))
        return builder.toString()
    }

    fun getFullProblem(): String? {
        val builder = StringBuilder()
        builder.append(problemHead)
        builder.append(problem)
        if (printer != null) builder.append(String.format("\\n\\nPrinter Name: %s\\nPrinter SN: %s", printer!!.getName(), printer!!.serialNumber))
        builder.append(problemTail)
        return builder.toString()
    }

    fun getFormVal(id: String, contents: String?): String {
        if (contents == null) return ""
        return String.format("document.getElementById('%s').value=\"%s\"\n", id, contents)
    }
}