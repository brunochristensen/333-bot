# https://discordpy.readthedocs.io/en/stable/

import discord
from discord.ext import commands
from accountabilitycog import AccountabilityCog
from dotenv import load_dotenv
import logging
import os

load_dotenv()

class Bot(commands.Bot):
  def __init__(self):
    intents = discord.Intents.default()
    intents.message_content = True
    super().__init__(command_prefix=commands.when_mentioned_or('!'), intents=intents)
    
  async def on_ready(self):
    print(f'Logged in as {self.user}' )
    await self.add_cog(AccountabilityCog(self))

bot = Bot()

@bot.event
async def on_message(message):
  #on_message triggers on the bot's own messages
  if message.author == bot.user:
    return
  #Stops on_message from preventing @client.command() being executed
  await bot.process_commands(message)

@bot.command()
async def hello(ctx):
  await ctx.send('Hello!')

token = os.getenv("DISCORD_TOKEN") or ""
log_handler = logging.FileHandler(filename='discord.log', encoding='utf-8', mode='w')
bot.run(token, log_handler=log_handler, log_level=logging.DEBUG)

