package guiview
/*import com.google.gson.Gson
import com.google.gson.reflect.TypeToken*/
import javax.swing.*
import javax.swing.table.DefaultTableModel
import socketinterface.createTournament
import java.awt.*
import javax.swing.border.Border


class GUIMain : JFrame("Nope Card Game") {

    private val scoreTable = JTable()
    private val menuPanel = JPanel()
    private val scorePanel = JPanel()
    private val crTournamentPanel = JPanel()
    private val tournamentLobbyPanel = JPanel()
    private val inGamePanel = JPanel()
    private val cardLayout = CardLayout()

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(1080, 720)


        // Show the window
        isVisible = true

        // Create a JPanel to hold the menu items
        menuPanel.layout = BoxLayout(menuPanel, BoxLayout.Y_AXIS)
        menuPanel.setBackground(Color.WHITE);

        // Create a JLabel for the title
        val titleLabel = JLabel("Welcome to the Start Menu")
        titleLabel.alignmentX = JComponent.CENTER_ALIGNMENT
        titleLabel.font = titleLabel.font.deriveFont(24f)
        menuPanel.add(titleLabel)

        // Create a JButton for each menu item
        val items = listOf("Create Tournament", "Join Tournament", "High Score List","game room")
        for (item in items) {
            val button = JButton(item)
            button.alignmentX = JComponent.CENTER_ALIGNMENT
            button.setForeground(Color.BLACK)                    // Vordergrundfarbe auf "rot" setzen
            button.background = Color(233,196,183) // Hintergrundfarbe auf "weiß" setzen
            button.isBorderPainted = false

            if(item == "Create Tournament"){
                button.addActionListener{
                    cardLayout.show(contentPane, "Create a new Tournament")
                }
            }
            if(item == "Join Tournament"){
                button.addActionListener{
                    cardLayout.show(contentPane, "tournamentLobby")
                }
            }
            if (item == "High Score List") {
                button.addActionListener {
                    cardLayout.show(contentPane, "scores")
                    //updateScoreTable()
                }
            }
            if(item == "game room")   {
                button.addActionListener {
                    cardLayout.show(contentPane, "game")

                }

            } else {
                button.addActionListener {
                    cardLayout.show(contentPane, "menu")
                }
            }
            menuPanel.add(button)
        }

        // Create a JButton to exit the application
        val exitButton = JButton("Exit")
        exitButton.alignmentX = JComponent.CENTER_ALIGNMENT
        exitButton.background = Color(233,196,183)
        exitButton.addActionListener {
            dispose()
        }
        menuPanel.add(exitButton)



        // Create a JPanel to hold the score table
        scorePanel.layout = BorderLayout()

        // Create a JTable for the score list
        scoreTable.model = DefaultTableModel(arrayOf("Name", "Score"), 0)
        scoreTable.fillsViewportHeight = true

        // Create a JScrollPane for the score table
        val scoreScrollPane = JScrollPane(scoreTable)
        scorePanel.add(scoreScrollPane, BorderLayout.CENTER)

        // Create a JButton to return to the menu
        val returnButton = JButton("Return")
        returnButton.addActionListener {
            cardLayout.show(contentPane, "menu")
        }
        scorePanel.add(returnButton, BorderLayout.SOUTH)




        initTournamentPanel()





        tournamentLobbyPanel.layout = BorderLayout()
        // Create a JButton to return to the menu
        val returnButton3 = JButton("leave Room")
        returnButton3.addActionListener {
            cardLayout.show(contentPane, "menu")
        }



        val joinTournament = JButton("Join/ Start Tournament")
        joinTournament.addActionListener {
            cardLayout.show(contentPane, "game")
        }


        val scrollPane = JScrollPane()
        scrollPane.background = Color(255,255,255)



        tournamentLobbyPanel.add(returnButton3, BorderLayout.SOUTH)
        tournamentLobbyPanel.add(joinTournament, BorderLayout.NORTH)
        tournamentLobbyPanel.add(scrollPane, BorderLayout.CENTER)





        inGamePanel.layout = BorderLayout()
        val returnButton4 = JButton("leave Tournament")
        returnButton4.addActionListener {
            cardLayout.show(contentPane, "tournamentLobby")
        }
        inGamePanel.add(returnButton4, BorderLayout.SOUTH)



        // Add the menu and score panels to the frame
        contentPane.layout = cardLayout
        contentPane.add(menuPanel, "menu")
        contentPane.add(scorePanel, "scores")
        contentPane.add(crTournamentPanel, "Create a new Tournament")
        contentPane.add(tournamentLobbyPanel, "tournamentLobby")
        contentPane.add(inGamePanel, "game")
    }

    private fun initTournamentPanel() {
        crTournamentPanel.layout = BorderLayout()
        crTournamentPanel.background = Color(255, 255, 255)


        // Create a JButton to create a Tournament
        val createButton = JButton("Create Tournament")
        createButton.isEnabled = false
        createButton.font = Font("Arial", Font.BOLD, 14)
        createButton.preferredSize = Dimension(150, 40)
        createButton.background = Color(233,196,183)
        createButton.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        createButton.isBorderPainted = true

        // Create a JButton to return to the menu
        val returnButton2 = JButton("Back to Main Menu")
        returnButton2.font = Font("Arial", Font.BOLD, 14)
        returnButton2.preferredSize = Dimension(150, 40)
        returnButton2.background = Color(255,255,255)
        returnButton2.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)

        returnButton2.addActionListener {
            cardLayout.show(contentPane, "menu")
        }

        // divide main Panel into two by adding a new panel
        val centerPanel = JPanel()
        centerPanel.layout = BoxLayout(centerPanel, BoxLayout.Y_AXIS)
        centerPanel.background = Color(255, 255, 255)

        // create Tournament Field
        val label = JLabel("Select ~Best of Matches~ number :")
        label.font = Font("Arial", Font.BOLD, 30)
        label.preferredSize = Dimension(300, 30)
        label.horizontalAlignment = SwingConstants.CENTER
        label.verticalAlignment = SwingConstants.CENTER
        //label.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)

        val tfName = JTextField(5)
        tfName.font = Font("Arial", Font.PLAIN, 20)
        tfName.preferredSize = Dimension(100, 30)
        tfName.horizontalAlignment = SwingConstants.CENTER
        tfName.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
        tfName.addActionListener {
            // check if there is an input of integer
            if (tfName.text.matches(Regex("-?\\d+"))) {
                createButton.isEnabled = true
            } else {
                createButton.isEnabled = false
            }
        }
        createButton.addActionListener {
            val number = tfName.text.toIntOrNull()
            if (number != null) {
                createTournament(number)
                cardLayout.show(contentPane, "tournamentLobby")
                tfName.text = "" // clear textfield
            } else {
                showMessage(this, "Please enter a valid integer value between 3 and 7.")


            }
        }
        centerPanel.add(label)
        centerPanel.add(tfName)
//        crTournamentPanel.add(label, BorderLayout.LINE_START)
//        crTournamentPanel.add(tfName, BorderLayout.CENTER)

        crTournamentPanel.add(createButton, BorderLayout.SOUTH)
        crTournamentPanel.add(returnButton2, BorderLayout.NORTH)
        crTournamentPanel.add(centerPanel, BorderLayout.CENTER)
    }
    /*    private fun updateScoreTable() {
            val jsonString = """
                [
                    {"name": "John", "score": 100},
                    {"name": "Mary", "score": 90},
                    {"name": "Bob", "score": 80},
                    {"name": "Alice", "score": 70},
                    {"name": "David", "score": 60}
                ]
            """.trimIndent()

            val gson = Gson()
            val typeToken = object : TypeToken<List<Score>>() {}.type
            val scores = gson.fromJson<List<Score>>(jsonString, typeToken)

            scores.sortedByDescending { it.score }
                .forEach { score ->
                    val model = scoreTable.model as DefaultTableModel
                    model.addRow(arrayOf(score.name, score.score))
                }
        }*/
}

data class Score(val name: String, val score: Int)

/*
data class tournamentCreate(val numBestOfMatches: Int)
*/
