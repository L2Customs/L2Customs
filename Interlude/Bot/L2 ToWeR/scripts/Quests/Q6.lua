SCONFIG = L2TConfig.GetConfig();
moveDistance = 30;
ShowToClient("Q6", "Quest Searching for New Power [Elf] - Started");
LearnAllSkills();
MoveTo(-116651, 255544, -1429, moveDistance);
MoveTo(-116672, 255526, -1428, moveDistance);
TargetNpc("Gallint", 32980);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=3&option=1", "[532501]");
ClickAndWait("menu_select?ask=10325&reply=1", "\"I haven't met them yet, actually.\"");
ClickAndWait("quest_accept?quest_id=10325", "\"Wait! Who's my race master?\"");
MoveTo(-116672, 255526, -1428, moveDistance);
MoveTo(-116677, 255492, -1428, moveDistance);
MoveTo(-116722, 255389, -1429, moveDistance);
MoveTo(-116920, 255455, -1339, moveDistance);
MoveTo(-117251, 255572, -1298, moveDistance);
MoveTo(-117593, 255412, -1298, moveDistance);
TargetNpc("Cindet", 32148);
Talk();
ClickAndWait("talk_select", "Quest");

MoveTo(-117593, 255412, -1298, moveDistance);
MoveTo(-117236, 255520, -1298, moveDistance);
MoveTo(-117063, 255501, -1298, moveDistance);
MoveTo(-116692, 255428, -1431, moveDistance);
MoveTo(-116671, 255506, -1428, moveDistance);
TargetNpc("Gallint", 32980);
Talk();
ClickAndWait("talk_select", "Quest");
ClickAndWait("quest_choice?choice=7&option=1", "[532502]");
ClearTargets();
ShowToClient("Q6", "Quest Searching for New Power [Elf] - Finished");
