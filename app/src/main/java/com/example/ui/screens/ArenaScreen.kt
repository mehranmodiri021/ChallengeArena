package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.ChallengeItem
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalArenaPalette
import com.example.ui.viewmodel.ActiveMathQuestion
import com.example.ui.viewmodel.ActiveTarget
import com.example.ui.viewmodel.ArenaGameState
import com.example.ui.viewmodel.ArenaGameStatus

@Composable
fun ArenaScreen(
    gameState: ArenaGameState,
    onTargetTapped: (Int) -> Unit,
    onMathOptionSelected: (Int) -> Unit,
    onPlayQuickMatch: () -> Unit,
    onExitGame: () -> Unit,
    onPauseGame: () -> Unit = {},
    onResumeGame: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val lang = LocalAppLanguage.current
    val isFa = lang == AppLanguage.PERSIAN

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        when (gameState.status) {
            ArenaGameStatus.IDLE -> {
                ArenaIdleView(
                    onPlayQuickMatch = onPlayQuickMatch,
                    modifier = Modifier.fillMaxSize()
                )
            }

            ArenaGameStatus.COUNTDOWN -> {
                ArenaCountdownView(
                    seconds = gameState.countdownSeconds,
                    challenge = gameState.activeChallenge,
                    isFa = isFa,
                    modifier = Modifier.fillMaxSize()
                )
            }

            ArenaGameStatus.PLAYING, ArenaGameStatus.PAUSED -> {
                ArenaPlayingView(
                    gameState = gameState,
                    onTargetTapped = onTargetTapped,
                    onMathOptionSelected = onMathOptionSelected,
                    onPause = onPauseGame,
                    onExit = onExitGame,
                    isFa = isFa,
                    modifier = Modifier.fillMaxSize()
                )

                if (gameState.status == ArenaGameStatus.PAUSED) {
                    ArenaPauseDialog(
                        score = gameState.score,
                        onResume = onResumeGame,
                        onQuit = onExitGame
                    )
                }
            }

            ArenaGameStatus.FINISHED -> {
                ArenaFinishedView(
                    gameState = gameState,
                    onPlayAgain = {
                        val challenge = gameState.activeChallenge
                        if (challenge != null) {
                            onPlayQuickMatch()
                        }
                    },
                    onReturnToLobby = onExitGame,
                    isFa = isFa,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ArenaCountdownView(
    seconds: Int,
    challenge: ChallengeItem?,
    isFa: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    val infiniteTransition = rememberInfiniteTransition(label = "countdown_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdown_scale"
    )

    Column(
        modifier = modifier
            .padding(24.dp)
            .testTag("arena_countdown_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (challenge != null) {
            Text(
                text = if (isFa) challenge.titleFa else challenge.titleEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = palette.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = strings.countdownReady,
            fontSize = 15.sp,
            color = palette.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(palette.primary, palette.primary.copy(alpha = 0.4f))
                    )
                )
                .border(4.dp, GoldAccent, CircleShape)
        ) {
            Text(
                text = if (seconds > 0) "$seconds" else strings.countdownGo,
                fontSize = 54.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ArenaPauseDialog(
    score: Int,
    onResume: () -> Unit,
    onQuit: () -> Unit
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    Dialog(onDismissRequest = onResume) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = strings.pauseTitle,
                        tint = palette.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.pauseTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = palette.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${strings.roundScore}: $score",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("arena_pause_resume_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primary,
                        contentColor = palette.onPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = strings.resume, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onQuit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("arena_pause_quit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.surfaceVariant,
                        contentColor = palette.onSurface
                    )
                ) {
                    Text(text = strings.quitMatch, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ArenaIdleView(
    onPlayQuickMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(palette.surfaceVariant)
                .border(2.dp, palette.primary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = strings.arenaTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = palette.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strings.arenaSubtitle,
            fontSize = 13.sp,
            color = palette.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onPlayQuickMatch,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(52.dp)
                .testTag("arena_launch_quick_match_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.primary,
                contentColor = palette.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Bolt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.playNow,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun ArenaPlayingView(
    gameState: ArenaGameState,
    onTargetTapped: (Int) -> Unit,
    onMathOptionSelected: (Int) -> Unit,
    onPause: () -> Unit,
    onExit: () -> Unit,
    isFa: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val challenge = gameState.activeChallenge

    Column(modifier = modifier) {
        // TOP HUD: Exit, Pause, Score, Combo, Timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exit and Pause Controls
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .testTag("arena_exit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = palette.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onPause,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .testTag("arena_pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = strings.pause,
                        tint = palette.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Score & Combo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${gameState.score}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = palette.primary
                )
                if (gameState.combo > 1) {
                    Text(
                        text = "⚡ COMBO x${gameState.combo}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            // Timer Pill
            val isUrgent = gameState.secondsLeft <= 5
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isUrgent) Color(0xFFFF1744) else palette.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "⏱️ ${gameState.secondsLeft}s",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = if (isUrgent) Color.White else palette.onSurface
                )
            }
        }

        // Challenge Info Subheader
        if (challenge != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isFa) challenge.titleFa else challenge.titleEn,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.onSurfaceVariant
                )
            }
        }

        // BATTLEFIELD CANVAS
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.surface)
                .border(1.dp, palette.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            val fieldWidth = maxWidth
            val fieldHeight = maxHeight

            if (gameState.mathQuestion != null) {
                // Math Rush Mode
                MathSprintPlayArea(
                    question = gameState.mathQuestion,
                    onOptionSelected = onMathOptionSelected,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (gameState.currentTarget != null) {
                // Target Reflex Tap Mode
                val target = gameState.currentTarget
                val targetX = fieldWidth * target.posXFraction
                val targetY = fieldHeight * target.posYFraction

                TargetNode(
                    target = target,
                    onClick = { onTargetTapped(target.id) },
                    modifier = Modifier.offset(x = targetX, y = targetY)
                )
            }
        }
    }
}

@Composable
private fun TargetNode(
    target: ActiveTarget,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val infiniteTransition = rememberInfiniteTransition(label = "target_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_scale"
    )

    val targetColor = if (target.isGolden) GoldAccent else palette.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(target.sizeDp.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        targetColor,
                        targetColor.copy(alpha = 0.4f)
                    )
                )
            )
            .border(2.dp, Color.White, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("arena_target_node_${target.id}")
    ) {
        Text(
            text = if (target.isGolden) "⭐" else "🎯",
            fontSize = (target.sizeDp / 2.5).sp
        )
    }
}

@Composable
private fun MathSprintPlayArea(
    question: ActiveMathQuestion,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = strings.solveProblem,
            fontSize = 13.sp,
            color = palette.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Equation Card
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(100.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.surfaceVariant)
                .border(2.dp, palette.primary, RoundedCornerShape(18.dp))
        ) {
            Text(
                text = question.equation,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = palette.primary
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            question.options.forEachIndexed { index, optionVal ->
                Button(
                    onClick = { onOptionSelected(index) },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("math_option_button_$index"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primaryContainer,
                        contentColor = palette.onPrimaryContainer
                    )
                ) {
                    Text(
                        text = "$optionVal",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ArenaFinishedView(
    gameState: ArenaGameState,
    onPlayAgain: () -> Unit,
    onReturnToLobby: () -> Unit,
    isFa: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val isWin = gameState.score >= 200

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Victory / Defeat Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(if (isWin) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF1744).copy(alpha = 0.2f))
                .border(
                    2.dp,
                    if (isWin) Color(0xFF00E676) else Color(0xFFFF1744),
                    CircleShape
                )
        ) {
            Text(
                text = if (isWin) "🏆" else "💥",
                fontSize = 44.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isWin) strings.victory else strings.defeat,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = if (isWin) Color(0xFF00E676) else Color(0xFFFF1744)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.gameOver,
            fontSize = 13.sp,
            color = palette.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Score Card
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.finalScore,
                    fontSize = 12.sp,
                    color = palette.onSurfaceVariant
                )
                Text(
                    text = "${gameState.score}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = palette.primary
                )

                if (gameState.isNewHighScore) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⭐ رکورد جدید! NEW RECORD ⭐",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (gameState.isAntiCheatFlagged) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.antiCheatFlagged,
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚡ ${strings.xpEarned}", fontSize = 11.sp, color = palette.onSurfaceVariant)
                        Text(
                            text = "+${gameState.xpEarned} XP",
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            fontSize = 14.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🪙 ${strings.coinsEarned}", fontSize = 11.sp, color = palette.onSurfaceVariant)
                        Text(
                            text = "+${gameState.coinsEarned} سکه",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            fontSize = 14.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥 کمبو برتر", fontSize = 11.sp, color = palette.onSurfaceVariant)
                        Text(
                            text = "x${gameState.maxCombo}",
                            fontWeight = FontWeight.Bold,
                            color = palette.secondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .testTag("arena_play_again_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.primary,
                contentColor = palette.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = strings.retry, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onReturnToLobby,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .testTag("arena_return_lobby_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.surfaceVariant,
                contentColor = palette.onSurface
            )
        ) {
            Text(text = strings.returnToLobby, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
