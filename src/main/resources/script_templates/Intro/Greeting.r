# @String name
# @OUTPUT String greeting

# A Renjin script with parameters.
# It is the duty of the scripting framework to harvest
# the 'name' parameter from the user, and then display
# the 'greeting' output parameter, based on its type.

greeting <- paste('Hello, ', name, '!', sep = '')
