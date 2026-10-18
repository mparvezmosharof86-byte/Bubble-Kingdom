@Composable
fun BalancedGoldenHomeButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(if (isSelected) 10.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFF9C4),
                                Color(0xFFFDE047),
                                Color(0xFFF59E0B),
                                Color(0xFFB45309)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF312E81),
                                Color(0xFF1E1B4B)
                            )
                        )
                    }
                )
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    brush = if (isSelected) {
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFFFD54F), Color(0xFF78350F))
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color(0xFF818CF8).copy(alpha = 0.5f), Color(0xFF4338CA))
                        )
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (isSelected) Color(0xFF1E1B4B) else Color(0xFFC7D2FE),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Home",
            color = if (isSelected) Color(0xFFFDE047) else Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
        )
    }
}
