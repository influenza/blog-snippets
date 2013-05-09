This project provides a simple bash script to install [graphite](http://graphite.wikidot.com/) on a machine.

This script has only been tested on Ubuntu 10.04, so your mileage may vary! If you have 
improvements to make it work across a broader variety of environments, feel free to 
send me a pull request.

Finally, this is no replacement for real configuration management software. It's merely
intended to help save time when you're hand hacking a monitoring server together.

To use:
```bash
$ curl https://raw.github.com/influenza/blog-snippets/master/GraphiteInstaller/install.sh | bash -s
```
