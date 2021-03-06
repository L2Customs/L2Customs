SCONFIG = L2TConfig.GetConfig();
moveDistance = 30;
ShowToClient("Q18", "Quest Start of Fate - Started");
MoveTo(-117716, 256170, -1327, moveDistance);
MoveTo(-117659, 256324, -1327, moveDistance);
TargetNpc("Rivian", 32147);
Talk();
ClickAndWait("talk_select", "Quest.");
ClickAndWait("quest_choice?choice=0&option=1", "[536701]");
ClickAndWait("menu_select?ask=10331&reply=1", "\"Yes, I do.\"");
ClickAndWait("quest_accept?quest_id=10331", "\"I'll do it.\"");
ClearTargets();
MoveTo(-117659, 256324, -1327, moveDistance);
MoveTo(-117715, 256174, -1327, moveDistance);
MoveTo(-117823, 255879, -1327, moveDistance);
MoveTo(-117137, 255539, -1298, moveDistance);
MoveTo(-115467, 254700, -1519, moveDistance);
MoveTo(-114736, 254598, -1531, moveDistance);
MoveTo(-114566, 254270, -1531, moveDistance);
Sleep(5000);
MoveTo(-114519, 254059, -1533, moveDistance);
TargetNpc("Richard", 33123);
Talk();
Click("menu_select?ask=-6103&reply=1", "Ruins of Ye Sagira");
WaitForTeleport();
MoveTo(-109295, 237493, -2962, moveDistance);
MoveTo(-109378, 237578, -2962, moveDistance);
TargetNpc("Lakcis", 32977);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=3&option=1", "[536702]");
ClickAndWait("menu_select?ask=10331&reply=1", "\"I am. I'm not afraid.\"");
Click("menu_select?ask=10331&reply=10", "\"Let's go!\"");
WaitForTeleport();
MoveTo(-111769, 231928, -3170, moveDistance);
MoveTo(-111790, 231787, -3168, moveDistance);
TargetNpc("Sebion", 32978);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=0&option=1", "[536702]");
ClickAndWait("menu_select?ask=10331&reply=1", "\"Anything else to go on?\"");
ClickAndWait("menu_select?ask=10331&reply=2", "\"That's what I'm here for.\"");
ClearTargets();
MoveTo(-111790, 231787, -3168, moveDistance);
TargetNpc("Sebion", 32978);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=1&option=1", "[536702]");
Click("menu_select?ask=10331&reply=3", "\"I'm ready.\"");
WaitForTeleport();
Sleep(5000);
-- Quest state changed, ID: 10331, STATE: 3
MoveTo(-119930, 211158, -8590, moveDistance);
MoveTo(-119168, 211164, -8592, moveDistance);
MoveTo(-119120, 211188, -8592, moveDistance);
TargetNpc("Infiltration Officer", 19155);
local oficer = GetTarget();
Talk();
Click("menu_select?ask=-4200&reply=1", "\"Yes, I'm ready.\"");
ClearTargets();
Sleep(1000);
MoveTo(-119120, 211188, -8592, moveDistance);
SCONFIG.melee.me.enabled = true;
SCONFIG.melee.me.attackRange = 60;
SCONFIG.targeting.option = L2TConfig.ETargetingType.TT_RANGE_FROM_CHAR;
SCONFIG.targeting.range = 900;
SetPause(false);
repeat
 Sleep(1000);
until oficer:GetRangeTo(-117032, 212490, -8592) < 200;
SetPause(true);
MoveTo(-118348, 211482, -8593, moveDistance);
MoveTo(-118021, 211466, -8598, moveDistance);
MoveTo(-117037, 212438, -8592, moveDistance);
MoveTo(-117032, 212490, -8592, moveDistance);

Target(oficer);
Talk();
Click("menu_select?ask=-4202&reply=1", "\"Yes, I'm ready.\"");
ClearTargets();
Sleep(1000);
MoveTo(-117032, 212490, -8592, moveDistance);
SCONFIG.pickup.userPickup.mode = L2TConfig.EPickupMode.PICKUP_BEFORE;
SCONFIG.pickup.userPickup.pickupRange = 300;
SCONFIG.targeting.range = 1000;
SetPause(false);
repeat
 Sleep(1000);
until  GetQuestManager():GetQuestItemCount(17615)>2;
SetPause(true);
Sleep(100);
TargetNpc("Belis Verification System", 33215);
Talk();
ClickAndWait("menu_select?ask=-2353&reply=1", "Insert Mark of Belis");
Talk();
ClickAndWait("menu_select?ask=-2353&reply=1", "Insert Mark of Belis");
Talk();
ClickAndWait("menu_select?ask=-2353&reply=1", "Insert Mark of Belis");
ClearTargets();

MoveTo(-116553, 213080, -8590, moveDistance);
MoveTo(-116995, 213462, -8592, moveDistance);
MoveTo(-117362, 213819, -8592, moveDistance);
MoveTo(-117275, 213763, -8592, moveDistance);
MoveTo(-117216, 213654, -8592, moveDistance);
MoveTo(-117798, 214228, -8592, moveDistance);
Sleep(5000);
Target(oficer);
Talk();
Click("menu_select?ask=-4204&reply=1", "\"Ready!\"");
ClearTargets();
TargetNpc("Electricity Generator", 33216);
local generator = GetTarget();
ClearTargets();

SCONFIG.targeting.option = L2TConfig.ETargetingType.TT_RANGE_FROM_POINT;
SCONFIG.targeting.centerPoint.X = -117391;
SCONFIG.targeting.centerPoint.Y = 213917;
SCONFIG.targeting.centerPoint.Z = -8592;
SCONFIG.targeting.range = 900;
SCONFIG.potions.enabled = true;
SetPause(false);
repeat
Sleep(1000);
until generator:IsValid() == false or generator:IsAlikeDeath();
Sleep(5000);
SetPause(true);

MoveTo(-118239, 214631, -8597, moveDistance);
MoveTo(-118602, 214493, -8593, moveDistance);
MoveTo(-119137, 213842, -8592, moveDistance);
MoveTo(-119116, 213763, -8592, moveDistance);
MoveTo(-119116, 213763, -8592, moveDistance);

Sleep(3000);
ClearTargets();
TargetNpc("Infiltration Officer", 19155);
Talk();
Click("menu_select?ask=-4206&reply=1", "\"Let's do this.\"");
ClearTargets();
MoveTo(-118668, 213308, -8677, moveDistance);
repeat 
Sleep(1000);
until TargetNpc("Nemertess", 22984);
local bos = GetTarget();
SCONFIG.melee.me.enabled = true;
SCONFIG.melee.me.attackRange = 600;
SCONFIG.targeting.option = L2TConfig.ETargetingType.TT_RANGE_FROM_CHAR;
SCONFIG.targeting.range = 600;
SetPause(false);
repeat
Sleep(1000);
until bos:IsValid() == false or bos:IsAlikeDeath();
SetPause(true);
ClearTargets();
MoveTo(-118326, 212978, -8679, moveDistance);
TargetNpc("Infiltration Officer", 19155);
repeat
Sleep(1000);
until GetTarget():GetRangeTo(-118326, 212978, -8679) < 200;
Sleep(2000);
Talk();
Click("menu_select?ask=-4207&reply=1", "\"Ready!\"");
WaitForTeleport();
Sleep(5000);

MoveTo(-111791, 231782, -3168, moveDistance);
MoveTo(-111758, 231786, -3171, moveDistance);
MoveTo(-111792, 231799, -3168, moveDistance);
TargetNpc("Sebion", 32978);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=3&option=1", "[536702]");
ClearTargets();
UseItem(10650); -- Adventurer's Scroll of Escape
WaitForTeleport();
MoveTo(-114318, 260014, -1199, moveDistance);
MoveTo(-114359, 260010, -1199, moveDistance);
MoveTo(-114366, 260256, -1181, moveDistance);
TargetNpc("Pantheon", 32972);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=17&option=1", "[536702]");
ClickAndWait("menu_select?ask=10331&reply=1", "\"Saril's Necklace. It's for you.\"");
ClearTargets();
MoveTo(-114366, 260256, -1181, moveDistance);
MoveTo(-114375, 260088, -1199, moveDistance);
MoveTo(-114662, 259696, -1199, moveDistance);
MoveTo(-114563, 258841, -1199, moveDistance);
MoveTo(-114369, 258423, -1199, moveDistance);
MoveTo(-114405, 257398, -1151, moveDistance);
MoveTo(-114503, 257351, -1138, moveDistance);
MoveTo(-115063, 257650, -1138, moveDistance);
MoveTo(-115398, 257781, -1137, moveDistance);
MoveTo(-116340, 257748, -1512, moveDistance);
MoveTo(-116689, 256726, -1496, moveDistance);
MoveTo(-117065, 255660, -1309, moveDistance);
MoveTo(-117149, 255586, -1298, moveDistance);
MoveTo(-117587, 255765, -1298, moveDistance);
MoveTo(-117640, 255789, -1302, moveDistance);
MoveTo(-117786, 255879, -1327, moveDistance);
MoveTo(-117679, 256216, -1327, moveDistance);
MoveTo(-117682, 256340, -1327, moveDistance);
TargetNpc("Rivian", 32147);
Talk();
ClickAndWait("talk_select", "Quest.");
ClickAndWait("quest_choice?choice=6&option=1", "[536702]");
ClickAndWait("menu_select?ask=10331&reply=2", "\"Elven Knight.\"");
ClearTargets();
ShowToClient("Q18", "Quest Start of Fate - Finished");
