package com.example.bakalauradarbalietotne

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.bakalauradarbalietotne.composables.workoutInterface
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlinx.coroutines.*

class Workout(
    val exercise: String,
    val mode: Int,
    val context: Context,
    var tts: TextToSpeech
) {

    fun check() {
        Log.d("what", "Fun check ${DigitalSkeleton.currentPose}")
        Log.d("what", "Mode: ${if (mode == 1) "New record" else "Workout"}")
        Log.d("what", "Exercise: $exercise")

    }

    fun checkIfUserIsOnScreen(): Boolean {
        val pose = DigitalSkeleton.currentPose
        // if users body is not on screen
        if (!userIsOnScreen(pose)) {
            Log.d("chyck", "im in user is on screen")
            tts.stop()
            ttsSpeak("Your body is not on screen!")
            return false
        } else return true
    }

    private fun userIsOnScreen(pose: Pose?): Boolean {
        // if pose is empty -> false
        return if (pose?.allPoseLandmarks?.isEmpty() == true)
            return false
        else true
    }

    fun startExercise(startingPose: Pose, coroutineScope: CoroutineScope): Deferred<Any> {
        val job = coroutineScope.async {
            when (exercise) {
                "squat" -> countSqaut(startingPose, coroutineScope)
                "pushup" -> countPushup(startingPose, coroutineScope).await()
                "plank" -> countPlank(startingPose, coroutineScope)
                else -> return@async
            }
        }
        return job
    }

    fun countSqaut(startingPose: Pose, coroutineScope: CoroutineScope) {}

    fun countPushup(startingPose: Pose, coroutineScope: CoroutineScope): Deferred<Any> {
        return coroutineScope.async {
            ttsSpeak("Im in exercise started $exercise!")
            val startingPoseLeftShoulderLandmarkY =
                startingPose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.y
            val startingPoseLeftFootIndexLandmarkY =
                startingPose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)?.position?.y
            val currentPoseShoulderLandmarkList = mutableListOf<Float?>()
            if (startingPoseLeftFootIndexLandmarkY != null && startingPoseLeftShoulderLandmarkY != null) {
                Log.d("chyck2", "im before start time elapsed ")
                val startTime = System.currentTimeMillis()
                var timeElapsed = 0L
                var i = 0
                var repDone = false
                while (timeElapsed < 5000L && !repDone) {
                    Log.d("chyck2", "im after start time elapsed ")
                    timeElapsed = System.currentTimeMillis() - startTime
                    // vrb šeit veikt pārbaudes?
                    currentPoseShoulderLandmarkList.add(
                        DigitalSkeleton.currentPose?.getPoseLandmark(
                            PoseLandmark.LEFT_SHOULDER
                        )?.position?.y
                    )
                    Log.d(
                        "chyck2",
                        "countPushup: ${currentPoseShoulderLandmarkList.size} ${currentPoseShoulderLandmarkList[i]}"
                    )
                    Log.d(
                        "chyck2",
                        "shoulder: ${
                            DigitalSkeleton.currentPose?.getPoseLandmark(
                                PoseLandmark.LEFT_SHOULDER
                            )?.position?.y
                        } and ankle: $startingPoseLeftFootIndexLandmarkY"
                    )
                    // if user is on screen, shoulder landmark is seen and user touches the ground with chest
                    if (currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1] != null &&
                        currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1]!! >= startingPoseLeftFootIndexLandmarkY - 50F &&
                        userIsOnScreen(DigitalSkeleton.currentPose)
                    ) {
                        // check if form is right while doing pushup
                        if (checkIfBodyIsInStraightLine(DigitalSkeleton.currentPose)) {
/*                        Log.d(
                            "chyck2",
                            "Is formula right - size [-1]: ${currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1]}, actual size: ${currentPoseShoulderLandmarkList.size} "
                        )*/
                            val getUpTime = System.currentTimeMillis()
                            var getUpTimeElapsed = 0L
                            while (getUpTimeElapsed < 5000L && !repDone) {
                                getUpTimeElapsed = System.currentTimeMillis() - getUpTime
                                currentPoseShoulderLandmarkList.add(
                                    DigitalSkeleton.currentPose?.getPoseLandmark(
                                        PoseLandmark.LEFT_SHOULDER
                                    )?.position?.y
                                )
                                if (currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1] != null &&
                                    currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1]!! <= startingPoseLeftShoulderLandmarkY + 10F &&
                                    userIsOnScreen(DigitalSkeleton.currentPose)
                                ) {
                                    workoutInterface.repsDone++
                                    ttsSpeak("${workoutInterface.repsDone}")
                                    repDone = true
                                    delay(1000)
                                }
                                delay(100)
                            }
                        } else {
                            ttsSpeak("Your back is not straight while performing exercise!")
                            break
                        }
                    }
                    delay(100)
                }
                if (!repDone) {
                    when (mode) {
                        1 -> {
                            ttsSpeak("Workout is over! You did ${workoutInterface.repsDone} reps!")
                            workoutInterface.workoutProcess = false
                        }
                        2 -> ttsSpeak("You are doing it too slow! Try to do pushup in 5 second time diapason!")
                    }
                }
            } else Log.d("chyck2", "one of the landmark x positions is null")
            Log.d("chyck2", "end size countPushup: ${currentPoseShoulderLandmarkList.size}$")
        }
    }

    fun countPlank(startingPose: Pose, coroutineScope: CoroutineScope) {
        GlobalScope.launch {
            ttsSpeak("Im in exercise started!")
            delay(4000L)
            ttsSpeak("Im in exercise started! Second time")
        }
    }

    /*   fun workoutProcess(context: Context, tts: TextToSpeech) {
           Log.d("what", "Checkers")
           GlobalScope.launch {
               val pose = DigitalSkeleton.currentPose
               Log.d("what", "Checkers after pose")
               var savePose = pose
               while (!userIsOnScreen(savePose)) {
                   Log.d("variables", "$pose")
                   Log.d("variables", "$savePose")
                   delay(1000)
                   if (!userIsOnScreen(savePose)) {
                       ttsSpeak2(context, tts, "Please position your whole body on the screen").await()
                       savePose = pose
                       Log.d("voice2", "delayed info")
                   } else {
                       ttsSpeak2(context, tts, "Everything is fine, we can start").await()
                   }
               }
           }
       }*/
    // if user is not on screen


    //ttsSpeak(context, tts, "Get in the starting position")
    // startWorkout(context, tts, "plank")
/*           if (DigitalSkeleton.currentPose?.let {
                    it.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.y
                }!! > 400f
            ) {
            workoutInterface.repsDone++
            ttsSpeak(context, tts, "${workoutInterface.repsDone}")

            Log.d("Check", "Y coord: ${DigitalSkeleton.currentPose?.getPoseLandmark(PoseLandmark.LEFT_EYE)?.position?.y}")
            Log.d("Check", "repsDone: ${workoutInterface.repsDone}")
       }*/


// tanī checkCamerPerms var lai viņš atgriež true vai false un tad onClickā if pārbaudi vai true, tad startēt intent kopā ar putextra stringu ar exerciseID

    fun checkStartingPosition(coroutineScope: CoroutineScope): Pose? {
        //coroutineScope.launch {
        //ttsSpeak("Please get in the $exercise starting position")
        //delay(2000)
        when (exercise) {
            "squat" -> {
                checkSquatStartingForm()
            }
            "pushup" -> {
                // if form is good
                if (checkPushupStartingForm()) {
                    Log.d("chyck", "im in checking pushup form = true")
                    ttsSpeak("Form is good! You are ready to start!")
                    return DigitalSkeleton.currentPose

                } else {
                    Log.d("chyck", "im in checking pushup form = false")
                    //ttsSpeak("Your body is not in straight line")
                    return null
                }


            }
            "plank" -> {
                // if form is good
                if (checkPlankStartingForm()) {
                    Log.d("chyck", "im in checking plank form = true")
                    ttsSpeak("Form is good! You are ready to start!")
                    return DigitalSkeleton.currentPose

                } else {
                    Log.d("chyck", "im in checking plank form = false")
                    //ttsSpeak("Your body is not in straight line")
                    return null
                }

                // }
            }
        }
        return null
    }


    fun checkSquatStartingForm(): Boolean {
        DigitalSkeleton.currentPose.let {
            if (checkIfBodyIsInStraightLine(it) && checkIfElbowsUnderShoulders(it)) {
                Log.d("chyck2", "form is good, returning true")
                return true
            } else {
                Log.d("chyck2", "form is not good, going in when")
                when {
                    !checkIfBodyIsInStraightLine(it) -> {
                        ttsSpeak("Your body is not in a straight line!")
                        Log.d("chyck2", "back fuckup")
                        return false
                    }
                    !checkIfElbowsUnderShoulders(it) -> {
                        ttsSpeak("Your elbows are not under your shoulders!")
                        Log.d("chyck2", "elbows fuckup")
                        return false
                    }
                }
                Log.d("chyck2", "ready to return false")
                return false
            }
        }
    }

    fun checkPushupStartingForm(): Boolean {
        DigitalSkeleton.currentPose.let {
            if (checkIfBodyIsInStraightLine(it) && checkIfElbowsUnderShoulders(it)) {
                Log.d("chyck2", "form is good, returning true")
                // šo teorētiski vajag tikai testēšanā
                // ttsSpeak("Your body is in a straight line! Keep it up!")
                return true
            } else {
                Log.d("chyck2", "form is not good, going in when")
                when {
                    !checkIfBodyIsInStraightLine(it) -> {
                        ttsSpeak("Your body is not in a straight line!")
                        Log.d("chyck2", "back fuckup")
                        return false
                    }
                    !checkIfElbowsUnderShoulders(it) -> {
                        ttsSpeak("Your elbows are not under your shoulders!")
                        Log.d("chyck2", "elbows fuckup")
                        return false
                    }
                }
                Log.d("chyck2", "ready to return false")
                return false
            }
        }

    }


    fun checkPlankStartingForm(): Boolean {
        DigitalSkeleton.currentPose.let {
            if (checkIfBodyIsInStraightLine(it) && checkIfElbowsUnderShoulders(it) && checkIfWristsAreForward(
                    it
                )
            ) {
                Log.d("chyck2", "form is good, returning true")
                // šo teorētiski vajag tikai testēšanā
                // ttsSpeak("Your body is in a straight line! Keep it up!")
                return true
            } else {
                Log.d("chyck2", "form is not good, going in when")
                when {
                    !checkIfBodyIsInStraightLine(it) -> {
                        ttsSpeak("Your body is not in a straight line!")
                        Log.d("chyck2", "back fuckup")
                        return false
                    }
                    !checkIfElbowsUnderShoulders(it) -> {
                        ttsSpeak("Your elbows are not under your shoulders!")
                        Log.d("chyck2", "elbows fuckup")
                        return false
                    }
                    !checkIfWristsAreForward(it) -> {
                        ttsSpeak("Your wrists should be in front of you!")
                        Log.d("chyck2", "elbows fuckup")
                        return false
                    }
                }
                Log.d("chyck2", "ready to return false")
                return false
            }
        }
    }

    fun checkIfWristsAreForward(pose: Pose?): Boolean {
        val leftElbowLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.x
        val leftWristLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position?.x
        val rightElbowLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)?.position?.x
        val rightWristLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position?.x

        if (
            leftWristLandmarkX != null &&
            leftElbowLandmarkX != null &&
            rightWristLandmarkX != null &&
            rightElbowLandmarkX != null
        ) {
            Log.d(
                "chyck4",
                "leftElbowLandmarkX: $leftElbowLandmarkX, leftWristLandmarkX: $leftWristLandmarkX"
            )
            return ((leftWristLandmarkX > leftElbowLandmarkX + 10f) &&
                    (rightWristLandmarkX > rightElbowLandmarkX + 10f))
        } else {
            ttsSpeak("Some of the landmarks are not on screen")
            return false
        }
    }

    fun checkIfElbowsUnderShoulders(pose: Pose?): Boolean {
        val leftShoulderLandmarkX =
            pose?.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.x
        val leftElbowLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.x
        val rightShoulderLandmarkX =
            pose?.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position?.x
        val rightElbowLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)?.position?.x

        // check if all the needed landmarks are on screen
        if (
            leftShoulderLandmarkX != null &&
            leftElbowLandmarkX != null &&
            rightShoulderLandmarkX != null &&
            rightElbowLandmarkX != null
        ) {
            Log.d(
                "chyck2",
                "checking left elbow state: elbowX: $leftElbowLandmarkX, shoulderX: $leftShoulderLandmarkX"
            )
            Log.d(
                "chyck2",
                "checking right elbow state: elbowX: $rightElbowLandmarkX, shoulderX: $rightShoulderLandmarkX"
            )

            Log.d(
                "chyck2",
                "elbows t/f: left ${(leftElbowLandmarkX in leftShoulderLandmarkX - 20f..leftShoulderLandmarkX + 20f)}"
            )
            Log.d(
                "chyck2",
                "elbows t/f: right ${(rightElbowLandmarkX in rightShoulderLandmarkX - 20f..rightShoulderLandmarkX + 20f)}"
            )
            return ((leftElbowLandmarkX in leftShoulderLandmarkX - 15f..leftShoulderLandmarkX + 15f) &&
                    (rightElbowLandmarkX in rightShoulderLandmarkX - 15f..rightShoulderLandmarkX + 15f))

        } else {
            ttsSpeak("Some of the landmarks are not on screen")
            return false
        }
    }

    fun checkIfBodyIsInStraightLine(pose: Pose?): Boolean {
        val leftShoulderLandmarkY = pose?.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.y
        val leftAnkleLandmarkY = pose?.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position?.y
        val leftHipLandmarkY = pose?.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position?.y
        val leftKneeLandmarkY = pose?.getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position?.y
        val rightShoulderLandmarkY = pose?.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position?.y
        val rightAnkleLandmarkY = pose?.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position?.y
        val rightHipLandmarkY = pose?.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position?.y
        val rightKneeLandmarkY = pose?.getPoseLandmark(PoseLandmark.RIGHT_KNEE)?.position?.y

        // check if all the needed landmarks are on screen
        if (
            leftShoulderLandmarkY != null &&
            leftAnkleLandmarkY != null &&
            leftHipLandmarkY != null &&
            leftKneeLandmarkY != null &&
            rightShoulderLandmarkY != null &&
            rightAnkleLandmarkY != null &&
            rightHipLandmarkY != null &&
            rightKneeLandmarkY != null
        ) {
            // if both sides ankle, knee, hip and shoulder are not in line in range of 100, then spine is not straight
            return (((leftShoulderLandmarkY + leftAnkleLandmarkY) / 2) in leftHipLandmarkY - 50f..leftHipLandmarkY + 50f) &&
                    (((leftHipLandmarkY + leftAnkleLandmarkY) / 2) in leftKneeLandmarkY - 50f..leftKneeLandmarkY + 50f) &&
                    (((rightShoulderLandmarkY + rightAnkleLandmarkY) / 2) in rightHipLandmarkY - 50f..rightHipLandmarkY + 50f) &&
                    (((rightHipLandmarkY + rightAnkleLandmarkY) / 2) in rightKneeLandmarkY - 50f..rightKneeLandmarkY + 50f)
        } else {
            ttsSpeak("Some of the landmarks are not on screen")
            return false
        }
    }

    fun ttsSpeak(text: String) {
        //var _tts = tts
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                //tts.setSpeechRate(3f)
                tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                if (tts.isSpeaking) Log.d("voice", "tts is speaking right now")
            }

        }

    }

    fun ifUserIsReadySpeech() {
        when (mode) {
            1 -> {
                if (exercise == "plank")
                    ttsSpeak("You can start to perform $exercise as long as you can!")
                else
                    ttsSpeak("You can start to perform $exercise as many repetitions as you can!")
            }
            2 -> ttsSpeak("You can start to perform exercise $exercise !")
            else -> ttsSpeak("Error selecting exercise!")
        }
    }
}


/*    fun ttsSpeak(context: Context, text: String) {
        lateinit var mTTS3:TextToSpeech
        mTTS3 = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                mTTS3.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }
    }*/
