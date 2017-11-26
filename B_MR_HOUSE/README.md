BajtaHack 2017 - Code
=====================

Repository for projects created at the BajtaHack 2017 hackathon.

Participants should publish their code:

- Login to <a href="https://github.com/">GitHub</a>
- **Fork this repository**: <a href="https://github.com/gw0/BajtaHack2017-Code">https://github.com/gw0/BajtaHack2017-Code</a>
- Insert all code into a subdirectory with your team name and project name (eg. <code>A_HomeAI</code>)
- Prepare a short <code>README.md</code> (or .txt) with basic setup instructions (technologies used, which code to put where, and how to run it) (do not forget about scripts on SRM modules)
- Commit and push your changes, then send a <strong>pull request on GitHub</strong>
- By pushing your code, you agree to the terms of the <a href="https://opensource.org/licenses/AGPL-3.0">AGPLv3 license</a>


License
=======

Copyright &copy; 2017 *BajtaHack Team* &lt;<bajtahack@data-lab.si>&gt; and participants of the hackathon

All source code in this repository is licensed under the [GNU Affero General Public License 3.0+](LICENSE_AGPL-3.0.txt) (AGPL-3.0+). Note that it is mandatory to make all modifications and complete source code of any code publicly available to any user.

Insractions

Download and install android studio.
Open project folder in android studio.

On modul 1 set script :

/** b1 */
powled = '17'
POST('/phy/gpio/alloc', powled)
PUT('/phy/gpio/17/cfg/value', '{"dir":"out","mode":"floating","irq":"none","debouncing":0}')
PUT('/phy/gpio/17/value', '1')

btn = '24'
POST('/phy/gpio/alloc', btn)
PUT('/phy/gpio/' + btn + '/cfg/value', '{"dir":"in","mode":"pullup","irq":"none","debouncing":0}')

led = '27'
POST('/phy/gpio/alloc', led)
PUT('/phy/gpio/' + led + '/cfg/value', '{"dir":"out","mode":"floating","irq":"none","debouncing":0}')

POST('/phy/gpio/alloc, 23/')
PUT('/phy/gpio/23/cfg/value', '{"dir":"in","mode":"floating","irq":"none","debouncing":0}')

POST('/phy/i2c/alloc', '1')
POST('/phy/i2c/1/slaves/alloc', '64')
PUT('/phy/i2c/1/slaves/64/datasize/value', '2')

On module 2 set script: