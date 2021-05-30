package com.example.bakalauradarbalietotne

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.bakalauradarbalietotne.composables.getWorkoutProgram
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

    fun checkIfUserIsOnScreen(): Boolean {
        val pose = DigitalSkeleton.currentPose
        // if users body is not on screen
        if (!userIsOnScreen(pose)) {
            tts.stop()
            ttsSpeak("Your body is not on screen!")
            return false
        } else
            return true
    }

    private fun userIsOnScreen(pose: Pose?): Boolean {
        // if pose is empty -> false
        return if (pose?.allPoseLandmarks?.isEmpty() == true)
            return false
        else true
    }

    fun ttsSpeak(text: String) {
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    fun startExercise(startingPose: Pose, coroutineScope: CoroutineScope): Deferred<Any> {
        return coroutineScope.async {
            when (exercise) {
                "squat" -> countSqaut(startingPose, coroutineScope).await()
                "pushup" -> countPushup(startingPose, coroutineScope).await()
                "plank" -> countPlank(startingPose, coroutineScope).await()
                else -> return@async
            }
        }
    }

    fun countSqaut(startingPose: Pose, coroutineScope: CoroutineScope): Deferred<Any> {
        return coroutineScope.async {
            ttsSpeak("Go!")

            val startingPoseLeftKneeIndexLandmarkY =
                startingPose.getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position?.y
            val startingPoseLeftHipIndexLandmarkY =
                startingPose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position?.y
            val currentPoseHipLandmarkList = mutableListOf<Float?>()

            if (startingPoseLeftKneeIndexLandmarkY != null && startingPoseLeftHipIndexLandmarkY != null) {
                val startTime = System.currentTimeMillis()
                var timeElapsed = 0L
                var repDone = false
                while (timeElapsed < 10000L && !repDone && userIsOnScreen(DigitalSkeleton.currentPose)) {
                    timeElapsed = System.currentTimeMillis() - startTime
                    currentPoseHipLandmarkList.add(
                        DigitalSkeleton.currentPose?.getPoseLandmark(
                            PoseLandmark.LEFT_HIP
                        )?.position?.y
                    )
                    // check if form is right while doing squat
                    if (checkIfShouldersParalelToAnkles(DigitalSkeleton.currentPose)) {
                        // if user is on screen, hip landmark is seen and goes down enough
                        if (currentPoseHipLandmarkList[currentPoseHipLandmarkList.size - 1] != null &&
                            currentPoseHipLandmarkList[currentPoseHipLandmarkList.size - 1]!! >= startingPoseLeftKneeIndexLandmarkY - 50F &&
                            userIsOnScreen(DigitalSkeleton.currentPose)
                        ) {
                            val getUpStartTime = System.currentTimeMillis()
                            var getUpTimeElapsed = 0L
                            while (getUpTimeElapsed < 5000L && !repDone && userIsOnScreen(
                                    DigitalSkeleton.currentPose
                                )
                            ) {
                                getUpTimeElapsed = System.currentTimeMillis() - getUpStartTime
                                currentPoseHipLandmarkList.add(
                                    DigitalSkeleton.currentPose?.getPoseLandmark(
                                        PoseLandmark.LEFT_HIP
                                    )?.position?.y
                                )
                                // check if form is right while doing squat
                                if (checkIfShouldersParalelToAnkles(DigitalSkeleton.currentPose)) {

                                    if (currentPoseHipLandmarkList[currentPoseHipLandmarkList.size - 1] != null &&
                                        currentPoseHipLandmarkList[currentPoseHipLandmarkList.size - 1]!! <= startingPoseLeftHipIndexLandmarkY + 10F &&
                                        userIsOnScreen(DigitalSkeleton.currentPose)
                                    ) {
                                        workoutInterface.repsDone++
                                        ttsSpeak("${workoutInterface.repsDone}")
                                        repDone = true
                                        delay(100)
                                    }
                                } else {
                                    if (mode == 2) {
                                        delay(500)
                                        ttsSpeak("Your shoulders and ankles should be in line while getting up! Try again!")
                                        delay(2000)
                                        repDone = true
                                    }
                                    if (mode == 1) {
                                        delay(500)
                                        ttsSpeak("Your shoulders and ankles should be in line while getting up! Exercise over!")
                                        delay(2000)
                                        repDone = true
                                        workoutInterface.workoutProcess = false
                                        break
                                    }
                                }
                                delay(100)
                            }
                            delay(100)
                            if (!repDone) {
                                when (mode) {
                                    1 -> {
                                        ttsSpeak("You are doing it too slow! Workout is over! You did ${workoutInterface.repsDone} reps!")
                                        delay(5000)
                                        workoutInterface.workoutProcess = false
                                        break
                                    }
                                    2 -> {
                                        ttsSpeak("You are doing it too slow! Try to do $exercise in 5 second time diapason!")
                                        delay(3000)
                                    }
                                }
                            }
                        }
                    } else {
                        if (mode == 2) {
                            delay(500)
                            ttsSpeak("Your shoulders and ankles should be in line while getting down! Try again!")
                            delay(2000)
                            repDone = true
                            //break
                        }
                        if (mode == 1) {
                            delay(500)
                            ttsSpeak("Your shoulders and ankles should be in line while getting down! Exercise over!")
                            delay(2000)
                            repDone = true
                            workoutInterface.workoutProcess = false
                            break
                        }
                    }
                    delay(100)
                }
                delay(100)
                if (!repDone) {
                    when (mode) {
                        1 -> {
                            ttsSpeak("You are doing it too slow! Workout is over! You did ${workoutInterface.repsDone} reps!")
                            delay(5000)
                            workoutInterface.workoutProcess = false
                        }
                        2 -> {
                            ttsSpeak("You are doing it too slow! Try to do $exercise in 5 second time diapason!")
                            delay(3000)
                        }
                    }
                }
            }
        }
    }

    fun countPushup(startingPose: Pose, coroutineScope: CoroutineScope): Deferred<Any> {
        return coroutineScope.async {
            ttsSpeak("Go!")
            val startingPoseLeftShoulderLandmarkY =
                startingPose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.y
            val startingPoseLeftFootIndexLandmarkY =
                startingPose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)?.position?.y
            val currentPoseShoulderLandmarkList = mutableListOf<Float?>()
            if (startingPoseLeftFootIndexLandmarkY != null && startingPoseLeftShoulderLandmarkY != null) {
                val startTime = System.currentTimeMillis()
                var timeElapsed = 0L
                var repDone = false
                while (timeElapsed < 10000L && !repDone && userIsOnScreen(DigitalSkeleton.currentPose)) {
                    timeElapsed = System.currentTimeMillis() - startTime
                    currentPoseShoulderLandmarkList.add(
                        DigitalSkeleton.currentPose?.getPoseLandmark(
                            PoseLandmark.LEFT_SHOULDER
                        )?.position?.y
                    )
                    // check if form is right while doing pushup
                    if (checkIfBodyIsInStraightLine(DigitalSkeleton.currentPose)) {
                        // if user is on screen, shoulder landmark is seen and user touches the ground with chest
                        if (currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1] != null &&
                            currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1]!! >=
                            startingPoseLeftFootIndexLandmarkY - 50F &&
                            userIsOnScreen(DigitalSkeleton.currentPose)
                        ) {
                            val getUpStartTime = System.currentTimeMillis()
                            var getUpTimeElapsed = 0L
                            while (getUpTimeElapsed < 5000L && !repDone && userIsOnScreen(
                                    DigitalSkeleton.currentPose
                                )
                            ) {
                                getUpTimeElapsed = System.currentTimeMillis() - getUpStartTime
                                currentPoseShoulderLandmarkList.add(
                                    DigitalSkeleton.currentPose?.getPoseLandmark(
                                        PoseLandmark.LEFT_SHOULDER
                                    )?.position?.y
                                )
                                if (checkIfBodyIsInStraightLine(DigitalSkeleton.currentPose)) {
                                    if (currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1] != null &&
                                        currentPoseShoulderLandmarkList[currentPoseShoulderLandmarkList.size - 1]!! <=
                                        startingPoseLeftShoulderLandmarkY + 10F &&
                                        userIsOnScreen(DigitalSkeleton.currentPose)
                                    ) {
                                        workoutInterface.repsDone++
                                        ttsSpeak("${workoutInterface.repsDone}")
                                        repDone = true
                                        delay(100)
                                    }
                                } else {
                                    if (mode == 2) {
                                        delay(500)
                                        ttsSpeak("Your back is not straight while getting up! Try again!")
                                        delay(2000)
                                        repDone = true
                                    }
                                    if (mode == 1) {
                                        delay(500)
                                        ttsSpeak("Your back is not straight while getting up! Exercise over!")
                                        delay(2000)
                                        repDone = true
                                        workoutInterface.workoutProcess = false
                                        break
                                    }
                                }
                                delay(100)
                            }
                            delay(100)
                            if (!repDone) {
                                when (mode) {
                                    1 -> {
                                        ttsSpeak("You are doing it too slow! Workout is over! You did ${workoutInterface.repsDone} reps!")
                                        delay(5000)
                                        workoutInterface.workoutProcess = false
                                        break
                                    }
                                    2 -> {
                                        ttsSpeak("You are doing it too slow! Try to do $exercise in 5 second time diapason!")
                                        delay(3000)
                                    }
                                }
                            }
                        }
                    } else {
                        if (mode == 2) {
                            delay(500)
                            ttsSpeak("Your back is not straight while going down! Try again!")
                            delay(2000)
                            repDone = true
                            //break
                        }
                        if (mode == 1) {
                            delay(500)
                            ttsSpeak("Your back is not straight while going down! Exercise over!")
                            delay(2000)
                            repDone = true
                            workoutInterface.workoutProcess = false
                            break
                        }
                    }
                    delay(100)
                }
                delay(100)
                if (!repDone) {
                    when (mode) {
                        1 -> {
                            ttsSpeak("You are doing it too slow! Workout is over! You did ${workoutInterface.repsDone} reps!")
                            delay(5000)
                            workoutInterface.workoutProcess = false
                        }
                        2 -> {
                            ttsSpeak("You are doing it too slow! Try to do $exercise in 5 second time diapason!")
                            delay(3000)
                        }
                    }
                }
            }

        }
    }

    fun countPlank(startingPose: Pose, coroutineScope: CoroutineScope): Deferred<Any> {
        return coroutineScope.async {
            ttsSpeak("Try to hold this position as long as possible!")
            var currentPose = DigitalSkeleton.currentPose
            val workoutProgram = getWorkoutProgram("plank")

            while (userIsOnScreen(currentPose) && checkIfBodyIsInStraightLine(currentPose)) {
                currentPose = DigitalSkeleton.currentPose
                delay(1000)
                workoutInterface.timeCounter++
                if (mode == 2 && workoutInterface.timeCounter > workoutProgram[workoutInterface.currentSet]) {
                    workoutInterface.timeCounter = 0
                    return@async
                }
            }
            when (mode) {
                1 -> {
                    delay(500)
                    ttsSpeak("Your body is not in straight line! Exercise over!")
                    delay(500)
                    workoutInterface.workoutProcess = false
                }
                2 -> {
                    delay(500)
                    workoutInterface.timeCounter = 0
                    workoutInterface.currentSet++
                    ttsSpeak("Your body is not in straight line! Try again!")
                    delay(2000)
                }
            }
        }
    }

    fun checkStartingPosition(coroutineScope: CoroutineScope): Pose? {
        when (exercise) {
            "squat" -> {
                return if (checkSquatStartingForm()) {
                    ttsSpeak("$exercise form is good! You are ready to start!")
                    DigitalSkeleton.currentPose
                } else null
            }
            "pushup" -> {
                return if (checkPushupStartingForm()) {
                    ttsSpeak("$exercise form is good! You are ready to start!")
                    DigitalSkeleton.currentPose
                } else null
            }
            "plank" -> {
                return if (checkPlankStartingForm()) {
                    ttsSpeak("$exercise form is good! You are ready to start!")
                    DigitalSkeleton.currentPose
                } else null
            }
        }
        return null
    }

    fun checkSquatStartingForm(): Boolean {
        DigitalSkeleton.currentPose.let {
            if (checkIfShouldersParalelToAnkles(it)) {
                return true
            } else {
                when {
                    !checkIfShouldersParalelToAnkles(it) -> {
                        ttsSpeak("Your body is not in a straight line!")
                        return false
                    }
                }
                return false
            }
        }
    }

    fun checkPushupStartingForm(): Boolean {
        DigitalSkeleton.currentPose.let {
            if (checkIfBodyIsInStraightLine(it) && checkIfElbowsUnderShoulders(it)) {
                return true
            } else {
                when {
                    !checkIfBodyIsInStraightLine(it) -> {
                        ttsSpeak("Your body is not in a straight line!")
                        return false
                    }
                    !checkIfElbowsUnderShoulders(it) -> {
                        ttsSpeak("Your elbows are not under your shoulders!")
                        return false
                    }
                }
                return false
            }
        }

    }


    fun checkPlankStartingForm(): Boolean {
        DigitalSkeleton.currentPose.let {
            if (checkIfBodyIsInStraightLine(it) &&
                checkIfElbowsUnderShoulders(it) &&
                checkIfWristsAreForward(it)
            ) {
                return true
            } else {
                when {
                    !checkIfBodyIsInStraightLine(it) -> {
                        ttsSpeak("Your body is not in a straight line!")
                        return false
                    }
                    !checkIfElbowsUnderShoulders(it) -> {
                        ttsSpeak("Your elbows are not under your shoulders!")
                        return false
                    }
                    !checkIfWristsAreForward(it) -> {
                        ttsSpeak("Your wrists should be in front of you!")
                        return false
                    }
                }
                return false
            }
        }
    }

    fun checkIfShouldersParalelToAnkles(pose: Pose?): Boolean {
        val leftAnkleLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position?.x
        val leftShoulderLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.x
        val rightAnkleLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position?.x
        val rightShoulderLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position?.x

        val leftShoulderLandmarkZ = pose?.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position3D?.z
        val rightShoulderLandmarkZ =
            pose?.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position3D?.z

        if (
            leftAnkleLandmarkX != null &&
            leftShoulderLandmarkX != null &&
            rightAnkleLandmarkX != null &&
            rightShoulderLandmarkX != null &&
            leftShoulderLandmarkZ != null &&
            rightShoulderLandmarkZ != null
        ) {
            // if user is rotated with left side = check left landmarks, else check right landmarks
            if (leftShoulderLandmarkZ < rightShoulderLandmarkZ)
                return (leftShoulderLandmarkX in leftAnkleLandmarkX - 30f..leftAnkleLandmarkX + 30f)
            else
                return (rightShoulderLandmarkX in rightAnkleLandmarkX - 30f..rightAnkleLandmarkX + 30f)

        } else {
            ttsSpeak("Some of the landmarks are not on screen")
            return false
        }
    }

    fun checkIfWristsAreForward(pose: Pose?): Boolean {
        val leftElbowLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.x
        val leftWristLandmarkX = pose?.getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position?.x
        val rightElbowLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)?.position?.x
        val rightWristLandmarkX = pose?.getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position?.x

        val leftShoulderLandmarkZ = pose?.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position3D?.z
        val rightShoulderLandmarkZ =
            pose?.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position3D?.z

        if (
            leftWristLandmarkX != null &&
            leftElbowLandmarkX != null &&
            rightWristLandmarkX != null &&
            rightElbowLandmarkX != null &&
            leftShoulderLandmarkZ != null &&
            rightShoulderLandmarkZ != null
        ) {
            // if user is rotated with his left side to the camera
            if (leftShoulderLandmarkZ < rightShoulderLandmarkZ) {
                return (leftWristLandmarkX < leftElbowLandmarkX - 30f)
            }
            // if user is rotated with his right side to the camera
            else {
                return (rightWristLandmarkX > rightElbowLandmarkX + 30f)
            }
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
        val leftShoulderLandmarkZ = pose?.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position3D?.z
        val rightShoulderLandmarkZ =
            pose?.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position3D?.z
        // check if all the needed landmarks are on screen
        if (
            leftShoulderLandmarkX != null &&
            leftElbowLandmarkX != null &&
            rightShoulderLandmarkX != null &&
            rightElbowLandmarkX != null &&
            leftShoulderLandmarkZ != null &&
            rightShoulderLandmarkZ != null
        ) {
            // if user is rotated with left side = check left landmarks, else check right landmarks
            if (leftShoulderLandmarkZ < rightShoulderLandmarkZ)
                return leftElbowLandmarkX in leftShoulderLandmarkX - 10f..leftShoulderLandmarkX + 10f
            else
                return rightElbowLandmarkX in rightShoulderLandmarkX - 10f..rightShoulderLandmarkX + 10f

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
            // if both sides ankles, knees, hips and shoulders are not in line in range of 100, then spine is not straight
            return (((leftShoulderLandmarkY + leftAnkleLandmarkY) / 2) in leftHipLandmarkY - 30f..leftHipLandmarkY + 30f) &&
                    (((leftHipLandmarkY + leftAnkleLandmarkY) / 2) in leftKneeLandmarkY - 30f..leftKneeLandmarkY + 30f) &&
                    (((rightShoulderLandmarkY + rightAnkleLandmarkY) / 2) in rightHipLandmarkY - 30f..rightHipLandmarkY + 30f) &&
                    (((rightHipLandmarkY + rightAnkleLandmarkY) / 2) in rightKneeLandmarkY - 30f..rightKneeLandmarkY + 30f)
        } else {
            ttsSpeak("Some of the landmarks are not on screen")
            return false
        }
    }
}
