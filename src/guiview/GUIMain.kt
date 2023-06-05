package guiview

import nopegamelogic.*
import socketinterface.*
import java.awt.*
import java.awt.Color
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * a graphical user interface for the game 'Nope' that lets the player create/chose a Tournament and shows the game moves between ai-players during a tournament
 */
class GUIMain : JFrame("Nope Card Game") {

    private val scoreTable = JTable()
    private var tournamentTable = JTable()
    private var currentTournamentTable = JTable()
    private var currentTournamentWinnerTable = JTable()

    private var tournamentMessageBoard = JTextArea(20,10)
    private var gameMessageBoard = JTextArea(20,10)
    private var gameInfoTextArea = JTextArea(6, 10)
    private var turnInfoTextArea = JTextArea(6, 4)
    private var hand = JTextArea(7, 10)

    private var topCardButton = JButton()
    private var lastTopCardButton = JButton()

    private var inTournament = false
    private var tournamentCreator = false

    private val menuPanel = JPanel()
    private val scorePanel = JPanel()
    private val crTournamentPanel = JPanel()
    private val tournamentListPanel = JPanel()
    private val tournamentLobbyPanel = JPanel()

    private val inGamePanel = JPanel()
    private val cardLayout = CardLayout()

    /**
     * initialises all Game Panels
     */
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1080, 720)


        val img = ImageIcon("bin/cover.png")
        setIconImage(img.getImage())

        // Show the window
        isVisible = true

        // Create a JPanel to hold the menu items
        menuPanel.layout = BoxLayout(menuPanel, BoxLayout.Y_AXIS)
        menuPanel.setBackground(Color.WHITE)

        // Create a JLabel for the title
        val titleLabel = JLabel("Welcome to Nope!™")
        titleLabel.alignmentX = CENTER_ALIGNMENT
        titleLabel.font = titleLabel.font.deriveFont(24f)
        menuPanel.add(titleLabel)
        val subtitleLabel = JLabel("THE KNOCKOUT CARD GAME")
        subtitleLabel.alignmentX = CENTER_ALIGNMENT
        subtitleLabel.font = titleLabel.font.deriveFont(16f)
        menuPanel.add(subtitleLabel)
        menuPanel.add(Box.createRigidArea(Dimension(0,40)))
        // Create a JButton for each menu item
        val items = listOf("Create Tournament", "Join Tournament", "High Score List","game room")
        for (item in items) {
            val button = JButton(item)
            button.alignmentX = CENTER_ALIGNMENT
            button.preferredSize = Dimension(400,70)
            button.maximumSize = Dimension(800, 70)
            button.setForeground(Color.BLACK)
            button.background = Color(233,196,183)
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
            menuPanel.add(Box.createRigidArea(Dimension(0,40)))

        }

        // Create a JButton to exit the application
        val exitButton = JButton("Exit")
        exitButton.alignmentX = CENTER_ALIGNMENT
        exitButton.background = Color(233,196,183)
        exitButton.maximumSize = Dimension(400, 45)
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


        initCreateTournamentPanel()

        initTournamentListPanel()

        initTournamentLobbyPanel()

        initGamePanel()


        // Add the menu and score panels to the frame
        contentPane.layout = cardLayout
        contentPane.add(menuPanel, "menu")
        contentPane.add(scorePanel, "scores")
        contentPane.add(crTournamentPanel, "Create a new Tournament")
        contentPane.add(tournamentListPanel, "tournamentLobby")
        contentPane.add(tournamentLobbyPanel, "game lobby")
        contentPane.add(inGamePanel, "game")
    }

    private fun initGamePanel() {
        inGamePanel.layout = BorderLayout()
        inGamePanel.background = Color(0,0,0)

        // divide main Panel into two by adding a new panel
        val mainBoxPanel = JPanel()
        mainBoxPanel.layout = BoxLayout(mainBoxPanel, BoxLayout.Y_AXIS)
        mainBoxPanel.isVisible = true
        mainBoxPanel.background = Color(255, 255, 160)

        // south of mainBoxPanel
        hand.font = Font("Arial", Font.PLAIN, 18)
        updateHandCards()


        // Divided north of mainBoxPanel
        val boxPanelNorth = JPanel()
        boxPanelNorth.layout = BoxLayout(boxPanelNorth, BoxLayout.X_AXIS)
        boxPanelNorth.isVisible = true

        // Right side of north including Game Info Field and Game Message Board
        val borderPanel4GameBoardAndGameInfo = JPanel()
        borderPanel4GameBoardAndGameInfo.layout = BorderLayout()
        borderPanel4GameBoardAndGameInfo.background = Color(159, 200, 255)


        val boxPanel4GameBoardAndGameInfo = JPanel()
        boxPanel4GameBoardAndGameInfo.layout = BoxLayout(boxPanel4GameBoardAndGameInfo, BoxLayout.Y_AXIS)
        boxPanel4GameBoardAndGameInfo.isVisible = true

        // init gameInfo field
        gameInfoTextArea.font = Font("Arial", Font.PLAIN, 22)
        updateGameInfoTextArea()

        // init game message board
        gameMessageBoard.font = Font("Arial",Font.PLAIN,16)
        val scrollPane = JScrollPane(gameMessageBoard)
        scrollPane.background = Color(255, 255, 255)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        boxPanel4GameBoardAndGameInfo.add(gameInfoTextArea)
        boxPanel4GameBoardAndGameInfo.add(scrollPane)

        val gameInfo = JLabel()
        gameInfo.font= Font("Arial", Font.PLAIN, 22)
        gameInfo.text = "INFO BOARD"
        borderPanel4GameBoardAndGameInfo.add(boxPanel4GameBoardAndGameInfo,BorderLayout.CENTER)
        borderPanel4GameBoardAndGameInfo.add(gameInfo,BorderLayout.NORTH)

        // Left side of North

        val tempPanel2 = JPanel()
        tempPanel2.layout = GridBagLayout()
        tempPanel2.background =  Color(159, 200, 255)

        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.BOTH

        val cardPile = JLabel("Discard Pile Cards")


        cardPile.font = Font("Arial", Font.BOLD, 33)
        gbc.gridx = 0
        gbc.gridy = 0
        tempPanel2.add(cardPile,gbc)
        val text1 = JTextArea("current top card:")

        gbc.gridx = 0
        gbc.gridy = 1
        tempPanel2.add(text1,gbc)
        val text2 = JTextArea("last top card:")
        gbc.gridx = 1
        gbc.gridy = 1
        tempPanel2.add(text2,gbc)

        gbc.gridx = 0
        gbc.gridy = 2


        //topCardButton.preferredSize = Dimension(20, 240)
        topCardButton.icon = ImageIcon("bin/cover.png")
        topCardButton.background = Color(255,255,255)
//        lastTopCardButton.preferredSize = Dimension(20, 240)
        lastTopCardButton.background = Color(255,255,255)
        lastTopCardButton.icon = ImageIcon("bin/cover.png")
        updateTopCards()

        tempPanel2.add(topCardButton, gbc)

        gbc.gridx = 1
        gbc.gridy = 2
//        gbc.weightx = 0.6


        tempPanel2.add(lastTopCardButton, gbc)

        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 2
        gbc.gridheight = 2
        turnInfoTextArea.font = Font("Arial",Font.PLAIN,16)

        tempPanel2.add(turnInfoTextArea,gbc)




        boxPanelNorth.add(tempPanel2)
        boxPanelNorth.add(borderPanel4GameBoardAndGameInfo)


        mainBoxPanel.add(boxPanelNorth,gbc)
        mainBoxPanel.add(hand)




        val returnButton4 = JButton("Go to Tournament Overview")
        returnButton4.font = Font("Arial", Font.BOLD, 14)
        returnButton4.preferredSize = Dimension(150, 40)
        returnButton4.background = Color(255,255,255)
        returnButton4.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
        returnButton4.addActionListener {

            cardLayout.show(contentPane, "game lobby")
        }



        val invisibleButton = JButton(" ")
        invisibleButton.font = Font("Arial", Font.BOLD, 14)
        invisibleButton.preferredSize = Dimension(150, 40)
        invisibleButton.background = Color(255,255,255)
        invisibleButton.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
        invisibleButton.addActionListener {

            // clear current Message Board
            gameMessageBoard.text = ""
        }
        inGamePanel.add(invisibleButton,BorderLayout.NORTH)
        inGamePanel.add(mainBoxPanel, BorderLayout.CENTER)
        inGamePanel.add(returnButton4, BorderLayout.SOUTH)
    }

    private fun initTournamentLobbyPanel(){
        tournamentLobbyPanel.layout = BorderLayout()


        // divide main Panel into two by adding a new panel
        val tempPanel = JPanel()
        tempPanel.layout = BorderLayout()
        tempPanel.background = Color(255, 255, 255)


        val leaveTournament = JButton("Leave Tournament")
        leaveTournament.font = Font("Arial", Font.BOLD, 14)
        leaveTournament.preferredSize = Dimension(150, 40)
        leaveTournament.background = Color(233,196,183)
        leaveTournament.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        leaveTournament.isBorderPainted = true
        leaveTournament.addActionListener {
            if (inTournament) {
                val tournamentInfoStatus = leaveTournament()
                if(tournamentInfoStatus.success){
                    showMessage(this, "You left the tournament", 2000)
                    inTournament = false
                    tournamentCreator = false
                    // reset current tournament and game to nothing
                    currentTournament.bestOf = null
                    currentTournament.id = null
                    currentTournament.currentSize = null
                    currentTournament.status = null
                    currentTournament.players = null
                    currentTournament.createdAt = null
                    currentGame = GameState(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)
                    currentMatch = Match(null,null,null,null,null,null,null,null)
                    currentMove = Move(MoveType.NOPE,null,null,null,"Because I Can!")

                    clearAllMenus()

                    cardLayout.show(contentPane, "tournamentLobby")
                }else{
                    showMessage(this, tournamentInfoStatus.error.toString(),2000)
                }

            } else {
                showMessage(this, "You cannot leave a tournament as you ar not in one", 2000)
            }

        }
        val switchRoom = JButton("Go to Game Room")
        switchRoom.font = Font("Arial", Font.BOLD, 14)
        switchRoom.preferredSize = Dimension(150, 40)
        switchRoom.background = Color(233,196,183)
        switchRoom.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        switchRoom.isBorderPainted = true
        switchRoom.addActionListener{
            cardLayout.show(contentPane, "game")
        }

        val startTournament = JButton("Start Tournament")
        startTournament.font = Font("Arial", Font.BOLD, 14)
        startTournament.preferredSize = Dimension(150, 40)
        startTournament.background = Color(233,196,183)
        startTournament.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        startTournament.isBorderPainted = true
        startTournament.addActionListener {
            if (tournamentCreator) {
                val tournamentInfoStatus = startTournament()
                if(tournamentInfoStatus.success){
                    showMessage(this, "The Tournament starts now -> go to game room", 2000)
                    cardLayout.show(contentPane, "game")
                }else{
                    showMessage(this,tournamentInfoStatus.error.toString(),2000)
                }
            } else {
                showMessage(this, "Sorry but only the admin can start a game", 2000)
            }
        }

        tempPanel.add(switchRoom, BorderLayout.CENTER)
        tempPanel.add(leaveTournament, BorderLayout.WEST)
        tempPanel.add(startTournament, BorderLayout.EAST)



        val boxPanelCurrentTournament = JPanel()
        boxPanelCurrentTournament.layout = BoxLayout(boxPanelCurrentTournament, BoxLayout.Y_AXIS)


        currentTournamentTable.model =
            DefaultTableModel(arrayOf("ID", "Current Size", "Date", "status", "Players","BestOf"), 0)
        currentTournamentTable.fillsViewportHeight = true
        currentTournamentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        currentTournamentTable.rowSelectionAllowed = true
        currentTournamentTable.columnSelectionAllowed = false
        currentTournamentTable.font = Font("Arial", Font.ITALIC, 16)
        currentTournamentTable.gridColor = Color(233,196,183)

        val scrollPane = JScrollPane(currentTournamentTable)
        scrollPane.background = Color(255, 255, 255)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        boxPanelCurrentTournament.add(scrollPane)


        val boxPanelCurTouMessageAndBoard = JPanel()
        boxPanelCurTouMessageAndBoard.layout = BoxLayout(boxPanelCurTouMessageAndBoard, BoxLayout.X_AXIS)

        tournamentMessageBoard.font = Font("Arial", Font.PLAIN, 22)
        tournamentMessageBoard.append("Tournament Message Board \n")

        val scrollPaneMessageBoard = JScrollPane(tournamentMessageBoard)
        scrollPaneMessageBoard.background = Color(255, 255, 255)
        scrollPaneMessageBoard.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        boxPanelCurTouMessageAndBoard.add(scrollPaneMessageBoard)

        currentTournamentWinnerTable.model = DefaultTableModel(arrayOf("Player", "Points"), 0)
        currentTournamentWinnerTable.fillsViewportHeight = true
        currentTournamentWinnerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        currentTournamentWinnerTable.rowSelectionAllowed = true
        currentTournamentWinnerTable.columnSelectionAllowed = false
        currentTournamentWinnerTable.font = Font("Arial", Font.ITALIC, 18)
        currentTournamentWinnerTable.gridColor = Color(255,255,255)
        currentTournamentWinnerTable.background = Color(233,196,183)

        val scrollPaneWinner = JScrollPane(currentTournamentWinnerTable)
        scrollPaneWinner.maximumSize = Dimension(400,600)
        scrollPaneWinner.background =  Color(233,196,183)
        scrollPaneWinner.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        boxPanelCurTouMessageAndBoard.add(scrollPaneWinner)
        boxPanelCurrentTournament.add(boxPanelCurTouMessageAndBoard)


        tournamentLobbyPanel.add(boxPanelCurrentTournament, BorderLayout.CENTER)





       //tournamentLobbyPanel.add(gameInfo, BorderLayout.CENTER)
        tournamentLobbyPanel.add(tempPanel, BorderLayout.SOUTH)


    }
    private fun initTournamentListPanel() {
        tournamentListPanel.layout = BorderLayout()
        // Create a JButton to return to the menu
        val returnButton3 = JButton("leave Room")
        returnButton3.font = Font("Arial", Font.BOLD, 14)
        returnButton3.preferredSize = Dimension(150, 40)
        returnButton3.background = Color(255,255,255)
        returnButton3.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)

        returnButton3.addActionListener {
            cardLayout.show(contentPane, "menu")
        }


        tournamentTable.model =
            DefaultTableModel(arrayOf("Number", "ID", "Current Size", "Date", "status", "Players"), 0)
        tournamentTable.fillsViewportHeight = true
        tournamentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        tournamentTable.rowSelectionAllowed = true
        tournamentTable.columnSelectionAllowed = false
        tournamentTable.font = Font("Arial", Font.ITALIC, 16)
        tournamentTable.gridColor = Color(233,196,183)



        val scrollPane = JScrollPane(tournamentTable)
        scrollPane.background = Color(255, 255, 255)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        tournamentListPanel.add(scrollPane, BorderLayout.CENTER)

        val joinTournament = JButton("Join Tournament")
        joinTournament.font = Font("Arial", Font.BOLD, 14)
        joinTournament.preferredSize = Dimension(150, 40)
        joinTournament.background = Color(233,196,183)
        joinTournament.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        joinTournament.isBorderPainted = true
        joinTournament.addActionListener {

            val selectedRowIndex = tournamentTable.selectedRow
            if (selectedRowIndex != -1) {

                val tournamentId = tournamentList[selectedRowIndex].id
                // join tournament
                val tournamentInfoStatus = joinTournament(tournamentId!!)
                if(tournamentInfoStatus.success){
                    inTournament = true
                    showMessage(this, "You joined game number: $tournamentId ", 4000)
                    //updateTournamentList()

                    currentTournament.status = tournamentList[selectedRowIndex].status
                    currentTournament.createdAt =tournamentList[selectedRowIndex].createdAt
                    updateCurrentTournamentList()
                    cardLayout.show(contentPane, "game lobby")
                }
                else{
                    showMessage(this, tournamentInfoStatus.error.toString(), 2000)
                }

            } else {
                showMessage(this, "Please choose the tournament you want to join", 2000)
            }
        }


        tournamentListPanel.add(returnButton3, BorderLayout.NORTH)
        tournamentListPanel.add(joinTournament, BorderLayout.SOUTH)



    }
    private fun initCreateTournamentPanel() {
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

                val tournamentInfoStatus = createTournament(number)
                if(tournamentInfoStatus.success && tournamentInfoStatus.tournamentId != null){

                    showMessage(this,"Tournament created with ID: ${tournamentInfoStatus.tournamentId}, current size: ${tournamentInfoStatus.currentSize}, best of: ${tournamentInfoStatus.bestOf}",4000)
                    inTournament = true
                    tournamentCreator = true
                    //updateTournamentList()
                    currentTournament.id = tournamentInfoStatus.tournamentId
                    currentTournament.currentSize = tournamentInfoStatus.currentSize
                    currentTournament.bestOf = tournamentInfoStatus.bestOf

                    updateCurrentTournamentList()
                    cardLayout.show(contentPane, "game lobby")
                    tfName.text = "" // clear textfield
                } else{
                    showMessage(this,tournamentInfoStatus.error.toString(),4000)
                }

            } else {
                showMessage(this, "Please enter a valid integer value between 3 and 7.",2000)


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
    private fun updateTournament(){
        val model = tournamentTable.model as DefaultTableModel
        val tempTournamentList = mutableListOf<Tournament>()
        tempTournamentList.addAll(tournamentList.map { it.copy() })


        for(i in 0 until tempTournamentList.size ){
            val currTour = tempTournamentList[i]
            //println(currTour)


            var players = ""
            for(j in 0 until currTour.players!!.size){
                val currP = currTour.players!![j]
                players  += " ${currP.username} "
            }
            model.addRow(arrayOf(i, currTour.id, currTour.currentSize, currTour.createdAt, currTour.status, players))
        }
        model.fireTableDataChanged()
        model.fireTableRowsInserted(0, tournamentList.size)

    }

    /**
     * updates the list of tournaments
     */
    fun updateTournamentList(){

        tournamentTable.model= DefaultTableModel(arrayOf("Number", "ID", "Current Size", "Date", "status", "Players"), 0 )
        updateTournament()
        if(inTournament == true){
            tournamentTable.setEnabled(false)
        }else{
            tournamentTable.isEnabled = true
        }
    }

    /**
     * updates the ranking list of the current Tournament
     */
    fun updateCurrentTournamentScoreList(){
        currentTournamentWinnerTable.model = DefaultTableModel(arrayOf("Player","Score"),0)
        val model = currentTournamentWinnerTable.model as DefaultTableModel
        val playerScore = ArrayList<Player>()
        if (currentTournament.id != null){
            for(i in 0 until (currentTournament.players?.size ?: 0)){
                currentTournament.players?.let { playerScore.add(it[i]) }

            }
            playerScore.sortByDescending { it.score ; }
        }
        for(i in 0 until playerScore.size){
            model.addRow(arrayOf(playerScore[i].username,playerScore[i].score))

        }
    }
    private fun updateCurrentTournament(){
        if (currentTournament.id != null){
            if(currentTournament.message!=null){
                showMessage(this,currentTournament.message.toString(),2000)
            }
            var players = ""
            if(currentTournament.players != null){
                for(j in 0 until currentTournament.players!!.size){
                    val currP = currentTournament.players!![j]
                    players  += " ${currP.username} "
                }
            }


            val host = currentTournament.host?.username ?:""

            val model = currentTournamentTable.model as DefaultTableModel
            model.addRow(arrayOf( currentTournament.id, currentTournament.currentSize, currentTournament.createdAt, currentTournament.status, players, currentTournament.bestOf,host))
            tournamentMessageBoard.append( "${currentTournament.message?:""}\n")
            //tournamentMessageBoard.append("Winner of the Tournament: ${currentTournament.winner.toString()}\n")
            println("Tournament Info $currentTournament")
        }
    }

    /**
     * update menu lists with information about the current tournament
     */
    fun updateCurrentTournamentList(){

        if(currentTournament.status == "FINISHED"){
            // go to Tournament Lobby Room
            cardLayout.show(contentPane, "game lobby")
        }else if(currentTournament.status == "IN_PROGRESS"){
            // go to game room
            cardLayout.show(contentPane, "game")
        }

        currentTournamentTable.model= DefaultTableModel(arrayOf("ID", "Current Size", "Date", "status", "Players", "Best Of", "Host"), 0 )
        updateCurrentTournament()


    }

    /**
     * update menus with information about the current game
     */
    fun updateCurrentGameStatus(){

        // update player scores
        updateGameInfoTextArea()
        // update MessageBoards
        gameMessageBoard.append("${currentGame.message}\n")
        tournamentMessageBoard.append("${currentGame.message?:""}\n")


    }

    /**
     * update menus with information about the current match
     */
    fun updateCurrentMatchInfo(){

        updateGameInfoTextArea()

        gameMessageBoard.append("${currentMatch.message}\n")
        tournamentMessageBoard.append("${currentMatch.message?:""}\n")
    }
    private fun updateGameInfoTextArea(){
        gameInfoTextArea.text = ""
        gameInfoTextArea.append("Tournament id :   ${currentMatch.tournamentId?:""}\n")
        gameInfoTextArea.append("Match id :   ${currentGame.matchId?:""}\n")
        gameInfoTextArea.append("Game id :   ${currentGame.gameId?:""}\n")
        gameInfoTextArea.append("Round: ${currentMatch.round?:""}    (Best of ${currentMatch.bestOf?:""})\n")
        gameInfoTextArea.append("Players :  \n")
        gameInfoTextArea.append("        ${currentMatch.opponents?.get(0)?.username?:""} : points ${currentMatch.opponents?.get(0)?.score?:""} \n")
        gameInfoTextArea.append("        ${currentMatch.opponents?.get(1)?.username?:""} : points ${currentMatch.opponents?.get(1)?.score?:""} \n")
    }
    private fun clearAllMenus(){
        updateGameState()
        updateHandCards()
        updateTopCards()
        updateCurrentTournamentScoreList()
        updateCurrentTournamentList()
        tournamentMessageBoard.text= ""
        gameMessageBoard.text = ""
        gameInfoTextArea.text = ""
        turnInfoTextArea.text = ""
    }

    /**
     * print game relevant information inside the text field
     */
    fun updateGameState(){
        updateHandCards()
        updateTurnInfo()
        updateTopCards()

        gameMessageBoard.append("\nLast Player was ${currentGame.prevPlayerIdx?.let { currentGame.players?.get(it)?.username } ?:"nobody"}\n")
        gameMessageBoard.append("Last Top Card:\n")
        gameMessageBoard.append("${currentGame.lastTopCard?.type?:""} ${currentGame.lastTopCard?.color?:""} ${currentGame.lastTopCard?.value?:""} ${currentGame.lastTopCard?.selectValue?:""} ${currentGame.lastTopCard?.selectedColor?:""}\n")
        gameMessageBoard.append("Current Top Card:\n")
        gameMessageBoard.append("${currentGame.topCard?.type?:""} ${currentGame.topCard?.color?:""} ${currentGame.topCard?.value?:""} ${currentGame.topCard?.selectValue?:""} ${currentGame.topCard?.selectedColor?:""}\n")
        gameMessageBoard.append("Last Move:\n")
        gameMessageBoard.append("Type: ${currentGame.lastMove?.type?:"No last move"}\n")
        gameMessageBoard.append("Card1: ${currentGame.lastMove?.card1?.type?:""} ${currentGame.lastMove?.card1?.color?:""} ${currentGame.lastMove?.card1?.value?:""} ${currentGame.lastMove?.card1?.selectValue?:""} ${currentGame.lastMove?.card1?.selectedColor?:""}\n")
        gameMessageBoard.append("Card2: ${currentGame.lastMove?.card2?.type?:""} ${currentGame.lastMove?.card2?.color?:""} ${currentGame.lastMove?.card2?.value?:""} ${currentGame.lastMove?.card2?.selectValue?:""} ${currentGame.lastMove?.card2?.selectedColor?:""}\n")
        gameMessageBoard.append("Card3: ${currentGame.lastMove?.card3?.type?:""} ${currentGame.lastMove?.card3?.color?:""} ${currentGame.lastMove?.card3?.value?:""} ${currentGame.lastMove?.card3?.selectValue?:""} ${currentGame.lastMove?.card3?.selectedColor?:""}\n")
        gameMessageBoard.append("It's ${currentGame.currentPlayer?.username?:""}'s turn!\n")

        gameMessageBoard.append("Player: ${currentGame.players?.get(0)?.username?:""} Size of hand:${currentGame.players?.get(0)?.handSize?:""}\n")
        gameMessageBoard.append("Player: ${currentGame.players?.get(1)?.username?:""} Size of hand:${currentGame.players?.get(1)?.handSize?:""}\n\n")

    }

    /**
     * print the current move of the ai-player
     */
    fun updateCurrentMove(){
        gameMessageBoard.append("${currentGameMoveNotice.message} \n")
        gameMessageBoard.append("\n")
        gameMessageBoard.append("This is your Bots move: \n")
        gameMessageBoard.append("Type: ${currentMove.type}\n")
        gameMessageBoard.append("Card1: ${currentMove.card1?.type?:""} ${currentMove.card1?.color?:""} ${currentMove.card1?.value?:""} ${currentMove.card1?.selectValue?:""} ${currentMove.card1?.selectedColor?:""}\n")
        gameMessageBoard.append("Card2: ${currentMove.card2?.type?:""} ${currentMove.card2?.color?:""} ${currentMove.card2?.value?:""} ${currentMove.card2?.selectValue?:""} ${currentMove.card2?.selectedColor?:""}\n")
        gameMessageBoard.append("Card3: ${currentMove.card3?.type?:""} ${currentMove.card3?.color?:""} ${currentMove.card3?.value?:""} ${currentMove.card3?.selectValue?:""} ${currentMove.card3?.selectedColor?:""}\n")
        gameMessageBoard.append("\n")

    }
    private fun updateHandCards(){
        hand.text = ""
        hand.append("Your hand cards:\n")

        for(i in 0 until (currentGame.hand?.size ?: 0)){
            hand.append("Card ${i+1}: ${currentGame.hand?.get(i)?.getType()?:""}  ${currentGame.hand?.get(i)?.getColor()?:""}  ${currentGame.hand?.get(i)?.value?:""}  ${currentGame.hand?.get(i)?.selectValue?:""}  ${currentGame.hand?.get(i)?.selectedColor?:""}\n")
        }
    }
    private fun updateTurnInfo(){
        turnInfoTextArea.text = ""
        turnInfoTextArea.append("Current Player: ")
        turnInfoTextArea.append("${currentGame.currentPlayer?.username ?:""}\n")

        turnInfoTextArea.append("${currentGame.players?.get(0)?.username?:""} Size of hand:${currentGame.players?.get(0)?.handSize?:""}\n")
        turnInfoTextArea.append("${currentGame.players?.get(1)?.username?:""} Size of hand:${currentGame.players?.get(1)?.handSize?:""}\n")
        turnInfoTextArea.append("Size of deck of cards:${currentGame.drawPileSize?:""}\n")

    }
    private fun updateTopCards(){
        // update current top card
        topCardButton.text = ("${currentGame.topCard?.type?:""} ${currentGame.topCard?.color ?:""} ${currentGame.topCard?.value?:""} ${currentGame.topCard?.selectValue?:""} ${currentGame.topCard?.selectedColor?:""}")

        // change background depending on color
        if(currentGame.topCard?.color == nopegamelogic.Color.RED){
            topCardButton.icon = ImageIcon("bin/red.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.GREEN){
            topCardButton.icon = ImageIcon("bin/green.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.YELLOW){
            topCardButton.icon = ImageIcon("bin/yellow.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.BLUE){
            topCardButton.icon = ImageIcon("bin/blue.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.RED_GREEN){
            topCardButton.icon = ImageIcon("bin/redgreen.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.RED_BLUE){
            topCardButton.icon = ImageIcon("bin/redblue.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.RED_YELLOW){
            topCardButton.icon = ImageIcon("bin/redyellow.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.BLUE_GREEN){
            topCardButton.icon = ImageIcon("bin/bluegreen.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.YELLOW_BLUE){
            topCardButton.icon = ImageIcon("bin/yellowblue.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.YELLOW_GREEN){
            topCardButton.icon = ImageIcon("bin/yellowgreen.png")
        }else if(currentGame.topCard?.color == nopegamelogic.Color.MULTI){
            topCardButton.icon = ImageIcon("bin/multi.png")
        }else{
            topCardButton.icon = ImageIcon("bin/cover.png")
        }

        // update last top card
        if((currentGame.lastTopCard?.value != null) or (currentGame.lastTopCard?.type!=nopegamelogic.Type.NUMBER)){
            lastTopCardButton.text = ("${currentGame.topCard?.type?:""} ${currentGame.lastTopCard?.color ?:""} ${currentGame.lastTopCard?.value?:""} ${currentGame.lastTopCard?.selectValue?:""} ${currentGame.lastTopCard?.selectedColor?:""}")
            // change background depending on color
            if(currentGame.lastTopCard?.color == nopegamelogic.Color.RED){
                lastTopCardButton.icon = ImageIcon("bin/red.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.GREEN){
                lastTopCardButton.icon = ImageIcon("bin/green.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.YELLOW){
                lastTopCardButton.icon = ImageIcon("bin/yellow.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.BLUE){
                lastTopCardButton.icon = ImageIcon("bin/blue.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.RED_GREEN){
                lastTopCardButton.icon = ImageIcon("bin/redgreen.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.RED_BLUE){
                lastTopCardButton.icon = ImageIcon("bin/redblue.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.RED_YELLOW){
                lastTopCardButton.icon = ImageIcon("bin/redyellow.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.BLUE_GREEN){
                lastTopCardButton.icon = ImageIcon("bin/bluegreen.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.YELLOW_BLUE){
                lastTopCardButton.icon = ImageIcon("bin/yellowblue.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.YELLOW_GREEN){
                lastTopCardButton.icon = ImageIcon("bin/yellowgreen.png")
            }else if(currentGame.lastTopCard?.color == nopegamelogic.Color.MULTI){
                lastTopCardButton.icon = ImageIcon("bin/multi.png")
            }else{
                lastTopCardButton.icon = ImageIcon("bin/cover.png")
            }
        }
    }

}

/**
 * show a message in form of a pop-up notice for a given amount of time on the screen
 */
fun showMessage(fr: JFrame, content: String, time: Int) {
    val dialog = JDialog()
    dialog.setSize(900, 50)

    val x = fr.locationOnScreen.x + fr.width / 2 - dialog.width / 2
    val y = fr.locationOnScreen.y + fr.height / 8 - dialog.height / 2
    dialog.setLocation(x,y)
    dialog.isUndecorated = true

    val panel = JPanel()
    panel.layout = BorderLayout()
    panel.background = Color(229, 226, 248)

    val iconImage = ImageIcon("bin/info2.png").image
    val scaledIcon = ImageIcon(iconImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH))
    val iconLabel = JLabel(scaledIcon)
    iconLabel.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
    panel.add(iconLabel, BorderLayout.WEST)

    val message = JLabel(content)
    message.font= Font("Arial", Font.BOLD, 16)
    message.horizontalAlignment = SwingConstants.CENTER
    message.verticalAlignment = SwingConstants.CENTER
    message.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
    panel.add(message, BorderLayout.CENTER)

    dialog.contentPane.add(panel)

    // 1500
    val timer = Timer(time) { _ ->

        dialog.dispose()
    }
    timer.isRepeats = false
    timer.start()

    dialog.isVisible = true
}
