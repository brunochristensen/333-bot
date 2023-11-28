import discord
from discord.ext import commands, tasks
from dotenv import load_dotenv
import os


class AccountabilityCog(commands.Cog):
    def __init__(self, bot):
        load_dotenv()
        self.bot = bot
        self.channel = self.bot.get_channel(int(os.getenv("ACCOUNTABILITY_CHANNEL_ID")))
        self.struct = DataStruct(self.bot)
        self.my_task.start()

    # time=datetime.time(hour=0, minute=0))  #TODO: Implement DST
    @tasks.loop(minutes=1)
    async def my_task(self):
        await self.channel.send("Menus", view=SelectView())

    @my_task.before_loop
    async def before_sendmessage(self):
        await self.bot.wait_until_ready()
        #Delete old message
        #Purge data structure
        #reload data structure
        
        
class DataStruct():
    def __init__(self, bot):
        self.dict = dict()
        self.guild = bot.get_guild(int(os.getenv("GUILD_ID")))
        self.role = self.guild.get_role(int(os.getenv("STUDENT_ROLE_ID")))
        for user in self.role.members:
            self.dict[user.nick] = ""
    
    def add_data(self, nickname, text):
        self.dict.update({nickname: text})

class Select(discord.ui.Select):
    def __init__(self):
        options = [
            discord.SelectOption(label="Option 1", description="This is option 1!"),
            discord.SelectOption(label="Option 2", description="This is option 2!"),
            discord.SelectOption(label="Option 3", description="This is option 3!"),
        ]
        super().__init__(
            placeholder="Select an option", max_values=1, min_values=1, options=options
        )

    async def callback(self, interaction: discord.Interaction):
        if self.values[0] == "Option 1":
            interaction.user.display_name
            await interaction.response.send_message(
                content="This is the first option from the entire list!", ephemeral=True
            )

        elif self.values[0] == "Option 2":
            await interaction.response.send_message(
                "This is the second option from the list entire wooo!", ephemeral=True
            )
        elif self.values[0] == "Option 3":
            await interaction.response.send_message("Third One!", ephemeral=True)


class SelectView(discord.ui.View):
    def __init__(self, *, timeout=180):
        super().__init__(timeout=timeout)
        self.add_item(Select())
