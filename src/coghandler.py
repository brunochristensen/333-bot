import datetime

from discord.ext import commands, tasks

####################################
# Tasks
####################################
class AccountabilityCog(commands.Cog):

  def __init__(self, bot):
    self.bot = bot
    self.my_task.start()

  def cog_unload(self):
    self.my_task.cancel()

  @tasks.loop(time=datetime.time(hour=0, minute=0))  #TODO: Implement DST
  async def my_task(self):
    print("my task")
    