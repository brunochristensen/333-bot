import datetime
import discord
from discord.ext import commands, tasks

class AccountabilityCog(commands.Cog):

  def __init__(self, bot):
    self.bot = bot
    self.my_task.start()

  def cog_unload(self):
    self.my_task.cancel()

  @tasks.loop(time=datetime.time(hour=0, minute=0))  #TODO: Implement DST
  async def my_task(self):
    view = AccountabilityDropdownView()
    await self.bot.send('Pick your reason for absence:', view=view)
    
class AccountabilityDropdown(discord.ui.Select):
  def __init__(self):
    options = [
      discord.SelectOption(label='Sec+', description='Up early to go pass the exam'),
      discord.SelectOption(label='Sick Call', description='Remember to let an MTL know.')
    ]
    super().__init__(placeholder='Reason for absence:', min_values=1, max_values=1, options=options)

  async def callback(self, interaction: discord.Interaction):
    await interaction.response.send_message(f'Reason for absence: {self.values[0]}')

class AccountabilityDropdownView(discord.ui.View):
  def __init__(self):
    super().__init__()
    self.add_item(AccountabilityDropdown())
  