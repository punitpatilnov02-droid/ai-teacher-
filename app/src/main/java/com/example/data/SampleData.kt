package com.example.data

import com.example.model.*

object SampleData {

    val sampleDocuments = listOf(
        StudyDocument(
            id = "doc_calc_4",
            title = "Calculus Chapter 4",
            subjectType = SubjectType.MATH,
            format = "PDF",
            subtitle = "Derivatives & Integrals Mastery",
            pageCount = 38,
            summary = "Covers the foundations of Differential and Integral Calculus, including the Power Rule, Product Rule, Chain Rule, and the Fundamental Theorem of Calculus with geometric intuition.",
            content = """
                # Chapter 4: Differential and Integral Calculus

                ## 4.1 The Essence of the Derivative
                The derivative of a function f(x) represents the instantaneous rate of change:
                f'(x) = lim_{h -> 0} [f(x+h) - f(x)] / h

                ### Key Rules:
                1. Power Rule: d/dx [x^n] = n * x^(n-1)
                2. Product Rule: d/dx [u * v] = u' * v + u * v'
                3. Quotient Rule: d/dx [u / v] = (u' * v - u * v') / v^2
                4. Chain Rule: d/dx [f(g(x))] = f'(g(x)) * g'(x)

                ## 4.2 Integration & Fundamental Theorem
                Integration is the inverse process of differentiation (finding the accumulation of area under a curve).
                First Fundamental Theorem of Calculus:
                If F(x) = ∫_{a}^{x} f(t) dt, then F'(x) = f(x).

                Second Fundamental Theorem:
                ∫_{a}^{b} f(x) dx = F(b) - F(a), where F'(x) = f(x).
            """.trimIndent(),
            keyTakeaways = listOf(
                "Derivatives quantify tangent slopes and rates of change.",
                "Chain Rule is crucial when composite functions f(g(x)) appear.",
                "The Fundamental Theorem of Calculus unifies slopes with areas under curves."
            ),
            formulaOrEquations = listOf(
                "f'(x) = lim_{h->0} [f(x+h) - f(x)] / h",
                "d/dx [f(g(x))] = f'(g(x)) · g'(x)",
                "∫_{a}^{b} f(x) dx = F(b) - F(a)"
            ),
            uploadDate = "Today"
        ),
        StudyDocument(
            id = "doc_phys_mech",
            title = "Physics Mechanics",
            subjectType = SubjectType.PHYSICS,
            format = "Slides",
            subtitle = "Newtonian Dynamics & Quantum Limits",
            pageCount = 24,
            summary = "Rigorous investigation of classical mechanics: Newton's three laws of motion, conservation of momentum and energy, rotational dynamics, and early quantum limits.",
            content = """
                # Classical Mechanics & Conservation Laws

                ## 1. Newton's Three Axioms of Motion
                - First Law (Inertia): An object remains in its state of rest or uniform motion unless acted upon by a net external force.
                - Second Law: F_net = m * a = dp/dt
                - Third Law: For every action, there is an equal and opposite reaction (F_AB = -F_BA).

                ## 2. Work-Energy Theorem
                The net work done on a particle equals its change in kinetic energy:
                W_net = ΔK = (1/2) * m * v_f^2 - (1/2) * m * v_i^2

                ## 3. Intro to Quantum Limits
                At atomic distances, classical trajectories fail due to Heisenberg's Uncertainty Principle:
                Δx * Δp >= ħ / 2.
            """.trimIndent(),
            keyTakeaways = listOf(
                "Net force is strictly proportional to the rate of momentum change dp/dt.",
                "Mechanical energy is conserved when non-conservative forces do zero net work.",
                "Quantum wave-particle duality imposes fundamental limits on simultaneous position-momentum precision."
            ),
            formulaOrEquations = listOf(
                "F = m · a",
                "W = ∫ F · dr = ΔK",
                "Δx · Δp ≥ ℏ / 2"
            ),
            uploadDate = "Yesterday"
        ),
        StudyDocument(
            id = "doc_chem_org",
            title = "Organic Reaction Mechanisms",
            subjectType = SubjectType.CHEMISTRY,
            format = "Notes",
            subtitle = "Nucleophilic Substitution & Elimination",
            pageCount = 19,
            summary = "Detailed visual breakdown of SN1, SN2, E1, and E2 mechanisms, carbocation stability, steric hindrance, and solvent polarity effects.",
            content = """
                # Organic Chemistry: SN1 vs SN2

                ## SN2 Mechanism (Bimolecular)
                - Concerted single-step reaction.
                - Backside attack produces an inversion of stereochemical configuration (Walden inversion).
                - Rate law: Rate = k * [Electrophile] * [Nucleophile].
                - Favored by strong nucleophiles and polar aprotic solvents (e.g., DMSO, Acetone).
                - Substrate preference: Methyl > Primary > Secondary >> Tertiary (hindered).

                ## SN1 Mechanism (Unimolecular)
                - Multi-step reaction via planar carbocation intermediate.
                - Produces a racemic mixture (retention and inversion).
                - Rate law: Rate = k * [Electrophile].
                - Favored by stable carbocations (Tertiary > Secondary) and polar protic solvents (H2O, EtOH).
            """.trimIndent(),
            keyTakeaways = listOf(
                "SN2 is concerted with 100% stereochemical inversion; SN1 proceeds via planar carbocations with racemization.",
                "Polar aprotic solvents accelerate SN2 by keeping nucleophilic anions naked and energetic.",
                "Tertiary halides undergo SN1 or elimination due to steric crowding preventing backside attack."
            ),
            formulaOrEquations = listOf(
                "Rate(SN2) = k[R-X][Nu⁻]",
                "Rate(SN1) = k[R-X]",
                "Carbocation Stability: 3° > 2° > 1° > Methyl"
            ),
            uploadDate = "3 days ago"
        ),
        StudyDocument(
            id = "doc_bio_dna",
            title = "Cellular Bio & ATP Synthesis",
            subjectType = SubjectType.BIOLOGY,
            format = "PDF",
            subtitle = "Mitochondria, Glycolysis & Transcription",
            pageCount = 32,
            summary = "Explore cellular respiration stages: Glycolysis, Pyruvate oxidation, the Citric Acid (Krebs) cycle, and oxidative phosphorylation driven by the proton gradient.",
            content = """
                # Cellular Respiration & Molecular Genetics

                ## 1. Cellular Respiration Equation
                C6H12O6 + 6O2 -> 6CO2 + 6H2O + ~30-32 ATP

                ## 2. Four Sequential Stages
                1. Glycolysis: Occurs in the cytoplasm. Converts 1 glucose into 2 pyruvates, netting 2 ATP and 2 NADH.
                2. Pyruvate Oxidation: Transports into mitochondrial matrix, forming Acetyl-CoA.
                3. Citric Acid Cycle (Krebs): Generates 6 NADH, 2 FADH2, and 2 ATP per glucose.
                4. Oxidative Phosphorylation: Chemiosmosis driven by ATP synthase harnessing the proton-motive force across the inner mitochondrial membrane.
            """.trimIndent(),
            keyTakeaways = listOf(
                "Chemiosmotic ATP synthesis relies on an electrochemical proton gradient across the inner membrane.",
                "Oxygen serves as the terminal electron acceptor in the electron transport chain, forming water.",
                "DNA transcription copies genomic blueprints into mRNA, later translated on ribosomal machinery."
            ),
            formulaOrEquations = listOf(
                "C6H12O6 + 6 O2 → 6 CO2 + 6 H2O + ATP",
                "Proton Motive Force: Δp = Δψ - (2.3 RT/F)ΔpH"
            ),
            uploadDate = "Last week"
        ),
        StudyDocument(
            id = "doc_hist_rev",
            title = "History Lecture: Industrial Era",
            subjectType = SubjectType.HISTORY,
            format = "Lecture",
            subtitle = "Technological Revolutions & Urbanization",
            pageCount = 15,
            summary = "Analyzes the 18th-19th century transformation from agrarian handcraft economies to machine-driven urban industrial capitalism.",
            content = """
                # The First and Second Industrial Revolutions

                ## Causes and Catalysts
                - Britain's unique nexus: abundant coal/iron reserves, intellectual property patents, capital surplus, and naval trade supremacy.
                - James Watt's refined steam engine (1769) liberated factories from geographic river reliance.

                ## Socio-Economic Consequences
                - Rapid urbanization and demographic shifts into industrial centers (Manchester, Birmingham).
                - Rise of the industrial working class and subsequent labor movements (Chartism, early trade unions).
                - Emergence of classical political economy (Adam Smith) and critical responses (Karl Marx).
            """.trimIndent(),
            keyTakeaways = listOf(
                "Steam power fundamentally reorganized human labor and geographic demographic distribution.",
                "The shift sparked modern labor legislation, public health infrastructure, and compulsory schooling.",
                "Second Industrial Revolution (late 19th c.) shifted focus toward electricity, steel, and internal combustion."
            ),
            uploadDate = "Oct 24"
        ),
        StudyDocument(
            id = "doc_code_algo",
            title = "Computer Science: Algorithms",
            subjectType = SubjectType.CODING,
            format = "Notes",
            subtitle = "Trees, Dynamic Programming & Complexity",
            pageCount = 42,
            summary = "Fundamental algorithm analysis including asymptotic Big-O bounds, recursion trees, memoization, shortest path graph traversals (Dijkstra, A*), and dynamic programming.",
            content = """
                # Algorithms & Computational Complexity

                ## 1. Asymptotic Complexity (Big-O)
                - O(1): Constant time (Hash map lookup)
                - O(log N): Binary Search on sorted arrays
                - O(N): Linear scan
                - O(N log N): Optimal comparison-based sorting (MergeSort, QuickSort avg)
                - O(N^2): Nested loops (BubbleSort, SelectionSort)

                ## 2. Dynamic Programming Principles
                To apply DP, the problem must exhibit:
                1. Overlapping Subproblems: Recursion repeats identical computations.
                2. Optimal Substructure: Global optimum constructed from optimal sub-solutions.
            """.trimIndent(),
            keyTakeaways = listOf(
                "Mastery of Big-O allows predicting system scaling before writing a single line of production code.",
                "Dynamic programming converts exponential brute-force searches into polynomial time via memoization.",
                "Tree traversals (DFS/BFS) form the basis of compilers, graph databases, and route planning."
            ),
            formulaOrEquations = listOf(
                "Master Theorem: T(n) = a·T(n/b) + O(n^d)",
                "Space Complexity: O(V + E) for Adjacency List"
            ),
            uploadDate = "Oct 26"
        )
    )

    val sampleGoals = listOf(
        StudyGoal("g1", "Read Math Notes (Calculus Ch. 4)", SubjectType.MATH, isCompleted = true, xpReward = 50, dueTag = "Done"),
        StudyGoal("g2", "Ask AI about Quantum limits & Uncertainty", SubjectType.PHYSICS, isCompleted = false, xpReward = 40, dueTag = "Today"),
        StudyGoal("g3", "Practice 5 Organic Reaction Mechanisms", SubjectType.CHEMISTRY, isCompleted = false, xpReward = 60, dueTag = "Today"),
        StudyGoal("g4", "Complete 10-Question Coding Quiz", SubjectType.CODING, isCompleted = false, xpReward = 75, dueTag = "Tomorrow")
    )

    val sampleFlashcards = listOf(
        Flashcard("fc1", SubjectType.MATH, "What is the Product Rule in Calculus?", "d/dx [u · v] = u'·v + u·v'\nDerivative of first times second plus first times derivative of second.", "Think: u'v + uv'"),
        Flashcard("fc2", SubjectType.MATH, "What does the Second Fundamental Theorem of Calculus state?", "∫_{a}^{b} f(x) dx = F(b) - F(a), where F'(x) = f(x).", "Net change of antiderivative"),
        Flashcard("fc3", SubjectType.PHYSICS, "Define Newton's Second Law in terms of momentum.", "F_net = dp/dt (Force is the instantaneous rate of change of linear momentum).", "dp/dt"),
        Flashcard("fc4", SubjectType.PHYSICS, "What is Heisenberg's Uncertainty Principle formula?", "Δx · Δp ≥ ℏ / 2", "Planck's reduced constant over 2"),
        Flashcard("fc5", SubjectType.CHEMISTRY, "What is the key stereochemical feature of SN2 reactions?", "Walden Inversion (100% inversion of configuration due to backside nucleophilic attack).", "Backside attack"),
        Flashcard("fc6", SubjectType.BIOLOGY, "What is the terminal electron acceptor in aerobic respiration?", "Molecular Oxygen (O2), which accepts electrons and protons to form water (H2O).", "Forms H2O"),
        Flashcard("fc7", SubjectType.CODING, "What is the average time complexity of QuickSort?", "O(N log N) on average, O(N²) worst-case when unbalanced pivot occurs.", "Divide and conquer")
    )

    val sampleQuizzes = listOf(
        QuizQuestion(
            id = "q1",
            subjectType = SubjectType.MATH,
            question = "What is the derivative of f(x) = x³ · sin(x)?",
            options = listOf(
                "3x² · cos(x)",
                "3x² · sin(x) + x³ · cos(x)",
                "3x² · sin(x) - x³ · cos(x)",
                "x³ · cos(x)"
            ),
            correctIndex = 1,
            explanation = "Using the Product Rule d/dx [u · v] = u'v + uv': u = x³ (u' = 3x²), v = sin(x) (v' = cos(x)). Thus, 3x²·sin(x) + x³·cos(x).",
            hint = "Recall the product rule: u'v + uv'."
        ),
        QuizQuestion(
            id = "q2",
            subjectType = SubjectType.PHYSICS,
            question = "If net work done on a particle is positive, what happens to its kinetic energy?",
            options = listOf(
                "Kinetic energy remains constant",
                "Kinetic energy decreases",
                "Kinetic energy increases",
                "Potential energy becomes zero"
            ),
            correctIndex = 2,
            explanation = "By the Work-Energy Theorem (W_net = ΔK), positive net work directly increases the kinetic energy of the object.",
            hint = "W_net = ΔK"
        ),
        QuizQuestion(
            id = "q3",
            subjectType = SubjectType.CHEMISTRY,
            question = "Which type of solvent best accelerates an SN2 nucleophilic substitution?",
            options = listOf(
                "Polar protic (e.g. Water, Ethanol)",
                "Polar aprotic (e.g. Acetone, DMSO)",
                "Non-polar alkane (e.g. Hexane)",
                "Strong acidic solvent (e.g. Concentrated H2SO4)"
            ),
            correctIndex = 1,
            explanation = "Polar aprotic solvents solvate cations well while leaving nucleophilic anions unencumbered ('naked'), dramatically enhancing nucleophilicity.",
            hint = "Solvents without hydrogen bonding protons."
        ),
        QuizQuestion(
            id = "q4",
            subjectType = SubjectType.CODING,
            question = "What is the time complexity of searching for an element in a balanced Binary Search Tree (BST)?",
            options = listOf(
                "O(1)",
                "O(log N)",
                "O(N)",
                "O(N log N)"
            ),
            correctIndex = 1,
            explanation = "Because each comparison cuts the search space in half, a balanced tree of N nodes has height O(log N).",
            hint = "Each step halves the problem size."
        )
    )

    fun initialMessages(subject: SubjectType): List<ChatMessage> {
        val topic = when (subject) {
            SubjectType.MATH -> "Advanced Calculus & Linear Algebra"
            SubjectType.PHYSICS -> "Mechanics & Quantum Physics"
            SubjectType.CHEMISTRY -> "Organic Chemistry & Reaction Mechanisms"
            SubjectType.BIOLOGY -> "Cellular Biology & Genetics"
            SubjectType.HISTORY -> "World History & Civilization Milestones"
            SubjectType.ENGLISH -> "Literary Analysis & Rhetoric"
            SubjectType.CODING -> "Algorithms & Data Structures"
        }

        return listOf(
            ChatMessage(
                id = "init_1",
                sender = Sender.AI,
                text = "Hello! I'm your Brightstep AI Tutor. I can see you're studying $topic today. How can I help you visualize the concepts?",
                timestamp = "Just now",
                steps = listOf(
                    StepBreakdown(1, "Pick a Question or Concept", "Ask any question, paste an equation, or tap a quick chip below."),
                    StepBreakdown(2, "Interactive Breakdown", "I'll dissect the theory into crystal-clear steps with intuitive proofs."),
                    StepBreakdown(3, "Practice & Retention", "Test yourself with flashcards and quizzes generated from your materials.")
                ),
                quickReplies = listOf(
                    "Explain chain rule step-by-step",
                    "How does SN1 vs SN2 work?",
                    "Generate a 3-question quiz",
                    "Summarize key formulas"
                )
            )
        )
    }

    val subscriptionPlans = listOf(
        SubscriptionTier(
            id = "plan_monthly",
            title = "Monthly Series",
            priceDisplay = "₹999",
            periodDisplay = "/ 30 दिन",
            durationDays = 30,
            isPopular = false,
            features = listOf(
                "100 एआई वीडियो प्रश्नोत्तर",
                "बेसिक 3D ट्यूटर संवाद",
                "मानक सहायता (Standard Support)",
                "पीडीएफ नोट्स और गेमीफाइड क्विज़"
            ),
            buttonText = "Instamojo से खरीदें",
            accentColor = 0xFF3B82F6,
            allows3DVideo = false,
            allowsUnlimitedPdfGames = false
        ),
        SubscriptionTier(
            id = "plan_yearly",
            title = "Yearly Series (सर्वोत्तम मूल्य)",
            priceDisplay = "₹9,999",
            periodDisplay = "/ 365 दिन (वार्षिक)",
            durationDays = 365,
            isPopular = true,
            features = listOf(
                "असीमित एआई वीडियो व वॉयस सत्र",
                "उन्नत 3D अवतार और बॉस बैटल गेम",
                "प्राथमिक 24/7 सहायता (Priority Support)",
                "असीमित पीडीएफ गेमिंग व फ्लैशकार्ड्स",
                "सभी 7 विषय पूरी तरह अनलॉक"
            ),
            buttonText = "Instamojo से खरीदें",
            accentColor = 0xFFF59E0B,
            allows3DVideo = true,
            allowsUnlimitedPdfGames = true
        ),
        SubscriptionTier(
            id = "plan_global",
            title = "USA & Global Plan",
            priceDisplay = "$149",
            periodDisplay = "/ 365 दिन",
            durationDays = 365,
            isPopular = false,
            features = listOf(
                "वैश्विक सर्वर एक्सेस (USA & Global Low-Latency)",
                "बहुभाषी सहायता (Multi-language AI)",
                "वार्षिक योजना के सभी फीचर्स शामिल",
                "शून्य विलंबता प्राथमिकता जेमिनी एआई ब्रेन"
            ),
            buttonText = "Instamojo से खरीदें",
            accentColor = 0xFF8B5CF6,
            allows3DVideo = true,
            allowsUnlimitedPdfGames = true
        )
    )

    /**
     * अपलोड की गई पीडीएफ या नोट्स से सीधे गेम प्रश्न उत्पन्न करता है
     */
    fun generateGameQuestionsForDocument(document: StudyDocument): List<GameQuestion> {
        return when (document.subjectType) {
            SubjectType.MATH -> listOf(
                GameQuestion(
                    id = "gq_math_1",
                    question = "कैलकुलस अध्याय 4: यदि f(x) = x⁴, तो इसका अवकलज (Derivative) f'(x) क्या होगा?",
                    options = listOf("4x³", "3x⁴", "x³ / 4", "4x⁵"),
                    correctIndex = 0,
                    explanation = "पावर नियम (Power Rule) के अनुसार: d/dx [xⁿ] = n·xⁿ⁻¹। इसलिए 4·x⁴⁻¹ = 4x³।",
                    sourcePdfTitle = document.title,
                    pointValue = 150
                ),
                GameQuestion(
                    id = "gq_math_2",
                    question = "चेन नियम (Chain Rule) के अनुसार f(g(x)) का अवकलज क्या है?",
                    options = listOf("f'(g(x)) · g'(x)", "f'(g'(x))", "f'(x) · g'(x)", "f(x) + g'(x)"),
                    correctIndex = 0,
                    explanation = "चेन नियम के अनुसार बाहरी फलन का अवकलज गुणा आंतरिक फलन का अवकलज f'(g(x)) · g'(x) होता है।",
                    sourcePdfTitle = document.title,
                    pointValue = 200
                ),
                GameQuestion(
                    id = "gq_math_3",
                    question = "कैलकुलस का मूलभूत प्रमेय (Fundamental Theorem) क्या जोड़ता है?",
                    options = listOf("वक़्र की ढलान और क्षेत्रफल (Slopes & Areas)", "केवल त्रिकोणमिति", "संख्या रेखा और अभाज्य संख्याएं", "केवल प्रायिकता"),
                    correctIndex = 0,
                    explanation = "यह अवकलन (ढलान) और समाकलन (क्षेत्रफल) को एक-दूसरे की विपरीत प्रक्रिया के रूप में जोड़ता है।",
                    sourcePdfTitle = document.title,
                    pointValue = 250
                ),
                GameQuestion(
                    id = "gq_math_4",
                    question = "बॉस राउंड ⚔️: ∫ (3x² + 2x) dx का समाकलन क्या होगा?",
                    options = listOf("x³ + x² + C", "6x + 2 + C", "3x³ + 2x² + C", "x⁴ + C"),
                    correctIndex = 0,
                    explanation = "समाकलन करने पर 3(x³/3) + 2(x²/2) + C = x³ + x² + C!",
                    sourcePdfTitle = document.title,
                    pointValue = 300
                )
            )

            SubjectType.PHYSICS -> listOf(
                GameQuestion(
                    id = "gq_phys_1",
                    question = "न्यूटन के द्वितीय नियम के अनुसार बल (Force) किसके समानुपाती होता है?",
                    options = listOf("संवेग परिवर्तन की दर (dp/dt)", "केवल वेग (Velocity)", "वस्तु का आयतन", "केवल घर्षण बल"),
                    correctIndex = 0,
                    explanation = "न्यूटन का द्वितीय नियम कहता है कि F = dp/dt = m·a।",
                    sourcePdfTitle = document.title,
                    pointValue = 150
                ),
                GameQuestion(
                    id = "gq_phys_2",
                    question = "हाइजेनबर्ग का अनिश्चितता सिद्धांत (Uncertainty Principle) क्या निर्धारित करता है?",
                    options = listOf("Δx · Δp ≥ ℏ / 2", "E = mc²", "F = G(m₁m₂)/r²", "PV = nRT"),
                    correctIndex = 0,
                    explanation = "स्थिति और संवेग दोनों को एक साथ पूर्ण सटीकता से नहीं मापा जा सकता: Δx · Δp ≥ ℏ / 2।",
                    sourcePdfTitle = document.title,
                    pointValue = 200
                ),
                GameQuestion(
                    id = "gq_phys_3",
                    question = "कार्य-ऊर्जा प्रमेय (Work-Energy Theorem) के अनुसार कुल कार्य किसके बराबर होता है?",
                    options = listOf("गतिज ऊर्जा में परिवर्तन (ΔK)", "स्थितिज ऊर्जा में कमी", "कुल संवेग", "शून्य"),
                    correctIndex = 0,
                    explanation = "W_net = ΔK = (1/2)m(v_f² - v_i²)।",
                    sourcePdfTitle = document.title,
                    pointValue = 250
                )
            )

            else -> listOf(
                GameQuestion(
                    id = "gq_gen_1",
                    question = "${document.title} से प्रश्न: इस दस्तावेज़ का मुख्य केंद्रीय सिद्धांत क्या है?",
                    options = listOf(
                        document.keyTakeaways.getOrElse(0) { "मूल सिद्धांत का गहन विश्लेषण" },
                        "केवल सामान्य परिभाषाएं",
                        "असंगत ऐतिहासिक विवरण",
                        "कोई निष्कर्ष नहीं"
                    ),
                    correctIndex = 0,
                    explanation = "दस्तावेज़ की मुख्य सीख: ${document.keyTakeaways.getOrElse(0) { document.summary }}",
                    sourcePdfTitle = document.title,
                    pointValue = 150
                ),
                GameQuestion(
                    id = "gq_gen_2",
                    question = "परीक्षा स्कोर बढ़ाने के लिए सबसे महत्वपूर्ण कारक क्या है?",
                    options = listOf("सक्रिय पुनरावृत्ति और त्वरित अभ्यास (Active Recall)", "रटना", "केवल एक बार पढ़ना", "पुनरावृत्ति छोड़ना"),
                    correctIndex = 0,
                    explanation = "सक्रिय याददाश्त और क्विज़ अभ्यास से समझ 80% अधिक समय तक बनी रहती है!",
                    sourcePdfTitle = document.title,
                    pointValue = 200
                )
            )
        }
    }
}


