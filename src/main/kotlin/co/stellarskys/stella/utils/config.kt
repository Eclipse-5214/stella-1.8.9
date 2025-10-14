package co.stellarskys.stella.utils

import co.stellarskys.stella.Stella
import co.stellarskys.stella.features.msc.buttonUtils.ButtonLayoutEditor
import co.stellarskys.stella.utils.config.core.Config

public val config = Config("Stella", "Stella") {
    category("General"){
        subcategory("info") {
            textparagraph {
                configName = "info"
                name = "Stella"
                description = "Made by NEXD_"
            }

            button {
                configName = "website"
                name = "Website"
                description = "A link to stella's website"
            }
        }
    }

    category( "Dungeons") {
        subcategory( "Room Name") {
            toggle {
                configName = "showRoomName"
                name = "Show Room Name"
                description = "Shows the current dungeon rooms name in a hud"
            }

            toggle {
                configName = "roomNameChroma"
                name = "Chroma Room Name"
                description = "Makes the room name chroma (Requires SBA or Skyhanni)"
            }
        }

        subcategory("Terminals") {
            toggle {
                configName = "termNumbers"
                name = "Terminal Numbers"
                description = "Number the terminals in dungeons (for calling terms)"
                default = false
            }

            dropdown {
                configName = "termNumber"
                name = "Number"
                description = "What terminal number you want to call"
                options = listOf("1", "2", "3", "4", "All")
                default = 4
                shouldShow { settings -> settings["termNumbers"] as Boolean }
            }

            toggle {
                configName = "highlightTerms"
                name = "Highlight Terms"
                description = "Highlights the terminals"
                default = false
                shouldShow { settings -> settings["termNumbers"] as Boolean }
            }

            colorpicker {
                configName = "termColor"
                name = "Highlight Color"
                description = "The color to highlight the terminals"
                default = rgba(0, 255, 255, 255)
                shouldShow { settings ->
                    (settings["termNumbers"] as Boolean) &&
                            (settings["highlightTerms"] as Boolean)
                }
            }

            toggle {
                configName = "showTermClass"
                name = "Show Class"
                description = "Displays related class"
                default = false
                shouldShow { settings -> settings["termNumbers"] as Boolean }
            }

            toggle {
                configName = "classColor"
                name = "Highlight Class Color"
                description = "Highlights the terminals the color of the class"
                default = false
                shouldShow { settings ->
                    (settings["termNumbers"] as Boolean) &&
                            (settings["highlightTerms"] as Boolean) &&
                            (settings["showTermClass"] as Boolean)
                }
            }

            toggle {
                configName = "hideNumber"
                name = "Hide Number"
                description = "Hides the terminal number"
                default = false
                shouldShow { settings ->
                    (settings["termNumbers"] as Boolean) &&
                            (settings["showTermClass"] as Boolean)
                }
            }

            toggle {
                configName = "m7Roles"
                name = "M7 Roles"
                description = "Displays M7 roles instead"
                default = false
                shouldShow { settings ->
                    (settings["termNumbers"] as Boolean) &&
                            (settings["showTermClass"] as Boolean)
                }
            }

            dropdown {
                configName = "termClass"
                name = "M7 Class"
                description = "What class you are playing"
                options = listOf("Tank", "Mage", "Bers", "Arch", "All")
                default = 4
                shouldShow { settings ->
                    (settings["termNumbers"] as Boolean) &&
                            (settings["showTermClass"] as Boolean) &&
                            (settings["m7Roles"] as Boolean)
                }
            }

            toggle {
                configName = "termTracker"
                name = "Terminal Tracker"
                description = "Tracks terminals, devices, and levers"
                default = false
            }
        }

        subcategory("Block Overlay") {
            toggle {
                configName = "enableDungBlockOverlay"
                name = "Enable Block Overlay"
                description = "Replaces dungeon block textures with colored overlays"
                default = true
            }

            toggle {
                configName = "dungeonBlocksEverywhere"
                name = "Render Outside Dungeons"
                description = "Shows block overlays even outside of dungeons"
                default = false
                shouldShow { settings -> settings["enableDungBlockOverlay"] as Boolean }
            }

            colorpicker {
                configName = "dungCrackedColour"
                name = "Cracked Brick Color"
                description = "Color used for cracked stone bricks"
                default = rgba(255, 0, 255, 255)
            }

            colorpicker {
                configName = "dungDispenserColour"
                name = "Dispenser Color"
                description = "Color used for dispensers"
                default = rgba(255, 255, 0, 255)
            }

            colorpicker {
                configName = "dungLeverColour"
                name = "Lever Color"
                description = "Color used for levers"
                default = rgba(0, 255, 0, 255)
            }

            colorpicker {
                configName = "dungTripWireColour"
                name = "Tripwire Color"
                description = "Color used for tripwires"
                default = rgba(0, 255, 255, 255)
            }

            colorpicker {
                configName = "dungBatColour"
                name = "Bat Color"
                description = "Color used for dungeon bats"
                default = rgba(255, 100, 255, 255)
            }

            colorpicker {
                configName = "dungChestColour"
                name = "Chest Color"
                description = "Color used for normal dungeon chests"
                default = rgba(255, 150, 0, 255)
            }

            colorpicker {
                configName = "dungTrappedChestColour"
                name = "Trapped Chest Color"
                description = "Color used for trapped dungeon chests"
                default = rgba(255, 0, 0, 255)
            }
        }

        subcategory("Class Colors") {
            colorpicker {
                configName = "healerColor"
                name = "Healer Color"
                description = "Color used for Healer class"
                default = rgba(240, 70, 240, 255)
            }

            colorpicker {
                configName = "mageColor"
                name = "Mage Color"
                description = "Color used for Mage class"
                default = rgba(70, 210, 210, 255)
            }

            colorpicker {
                configName = "berzColor"
                name = "Berserker Color"
                description = "Color used for Berserker class"
                default = rgba(255, 0, 0, 255)
            }

            colorpicker {
                configName = "archerColor"
                name = "Archer Color"
                description = "Color used for Archer class"
                default = rgba(254, 223, 0, 255)
            }

            colorpicker {
                configName = "tankColor"
                name = "Tank Color"
                description = "Color used for Tank class"
                default = rgba(30, 170, 50, 255)
            }
        }
    }

    category("StellaNav") {
        subcategory("general") {
            toggle {
                configName = "mapEnabled"
                name = "Enable Map"
                description = "Enables the dungeon map"
                default = false
            }

            toggle {
                configName = "bossMapEnabled"
                name = "Enable Boss Map"
                description = "Enables the dungeon boss map"
                default = false
                shouldShow { settings -> settings["mapEnabled"] as Boolean }
            }

            toggle {
                configName = "scoreMapEnabled"
                name = "Enable Score Map"
                description = "Enables the dungeon score map"
                default = false
                shouldShow { settings -> settings["mapEnabled"] as Boolean }
            }

            toggle {
                configName = "mapInfoUnder"
                name = "Map Info Under Map"
                description = "Renders map info below the map"
                default = true
                shouldShow { settings -> settings["mapEnabled"] as Boolean }
            }
        }

        subcategory("Map") {
            colorpicker {
                configName = "mapBgColor"
                name = "Map Background Color"
                description = "Background color of the map"
                default = rgba(0, 0, 0, 100)
            }

            toggle {
                configName = "mapBorder"
                name = "Map Border"
                description = "Renders a border around the map"
                default = true
            }

            colorpicker {
                configName = "mapBdColor"
                name = "Map Border Color"
                description = "Color of the map border"
                default = rgba(0, 0, 0, 255)
                shouldShow { settings -> settings["mapBorder"] as Boolean }
            }

            stepslider {
                configName = "mapBdWidth"
                name = "Border Width"
                description = "The width of the map border"
                min = 1
                max = 5
                step = 1
                default = 2
                shouldShow { settings -> settings["mapBorder"] as Boolean }
            }

            dropdown {
                configName = "roomCheckmarks"
                name = "Room Checkmarks"
                description = "Style of room checkmarks"
                options = listOf("Checkmark", "Name", "Secrets", "Both")
                default = 0
            }

            dropdown {
                configName = "puzzleCheckmarks"
                name = "Puzzle Checkmarks"
                description = "Style of puzzle checkmarks"
                options = listOf("Checkmark", "Name", "Secrets", "Both")
                default = 0
            }

            slider {
                configName = "checkmarkScale"
                name = "Checkmark Size"
                description = "Size of the checkmarks"
                min = 0.1f
                max = 2f
                default = 1f
            }

            slider {
                configName = "rcsize"
                name = "Room Text"
                description = "Size of room text"
                min = 0.1f
                max = 2f
                default = 1f
            }

            slider {
                configName = "pcsize"
                name = "Puzzle Text"
                description = "Size of puzzle text"
                min = 0.1f
                max = 2f
                default = 1f
            }
        }

        subcategory("Player Icons") {
            slider {
                configName = "iconScale"
                name = "Icon Scale"
                description = "Scale of the player icons"
                min = 0.1f
                max = 2f
                default = 1f
            }

            toggle {
                configName = "showPlayerHeads"
                name = "Player Heads"
                description = "Use player heads instead of map markers"
                default = false
            }

            slider {
                configName = "iconBorderWidth"
                name = "Border Width"
                description = "The width of the icon border"
                min = 0f
                max = 1f
                default = 0.2f
            }

            colorpicker {
                configName = "iconBorderColor"
                name = "Border Color"
                description = "The color for the icon border"
                default = rgba(0,0,0,255)
            }

            toggle {
                configName = "iconClassColors"
                name = "Class Colors"
                description = "Use the color for the players class for the icon border"
                default = false
            }

            toggle {
                configName = "showNames"
                name = "Show Player Names"
                description = "Render player names under map icons"
            }

            toggle {
                configName = "dontShowOwn"
                name = "Hide Own Name"
                description = "Hides your name on the map"
                shouldShow { settings -> settings["showNames"] as Boolean }
            }

        }

        subcategory("Room Colors") {
            colorpicker {
                configName = "normalRoomColor"
                name = "Normal"
                default = rgba(107, 58, 17, 255)
            }
            colorpicker {
                configName = "puzzleRoomColor"
                name = "Puzzle"
                default = rgba(117, 0, 133, 255)
            }
            colorpicker {
                configName = "trapRoomColor"
                name = "Trap"
                default = rgba(216, 127, 51, 255)
            }
            colorpicker {
                configName = "minibossRoomColor"
                name = "Miniboss"
                default = rgba(254, 223, 0, 255)
            }
            colorpicker {
                configName = "bloodRoomColor"
                name = "Blood"
                default = rgba(255, 0, 0, 255)
            }
            colorpicker {
                configName = "fairyRoomColor"
                name = "Fairy"
                default = rgba(224, 0, 255, 255)
            }
            colorpicker {
                configName = "entranceRoomColor"
                name = "Entrance"
                default = rgba(20, 133, 0, 255)
            }
        }

        subcategory("Door Colors") {
            colorpicker {
                configName = "normalDoorColor"
                name = "Normal Door"
                default = rgba(80, 40, 10, 255)
            }
            colorpicker {
                configName = "witherDoorColor"
                name = "Wither Door"
                default = rgba(0, 0, 0, 255)
            }
            colorpicker {
                configName = "bloodDoorColor"
                name = "Blood Door"
                default = rgba(255, 0, 0, 255)
            }
            colorpicker {
                configName = "entranceDoorColor"
                name = "Entrance Door"
                default = rgba(0, 204, 0, 255)
            }
        }

        subcategory("extra") {
            toggle {
                configName = "boxWitherDoors"
                name = "Box Wither Doors"
                description = "Renders a box around wither doors"
                default = false
            }

            colorpicker {
                configName = "keyColor"
                name = "Key Color"
                description = "Color for doors with keys"
                default = rgba(0, 255, 0, 255)
                shouldShow { settings -> settings["boxWitherDoors"] as Boolean }
            }

            colorpicker {
                configName = "noKeyColor"
                name = "No Key Color"
                description = "Color for doors without keys"
                default = rgba(255, 0, 0, 255)
                shouldShow { settings -> settings["boxWitherDoors"] as Boolean }
            }

            stepslider {
                configName = "doorLineWidth"
                name = "Door Line Width"
                description = "Line width for doors"
                min = 1
                max = 5
                step = 1
                default = 3
                shouldShow { settings -> settings["boxWitherDoors"] as Boolean }
            }

            toggle {
                configName = "separateMapInfo"
                name = "Separate Map Info"
                description = "Renders the map info separate from the dungeon map"
                default = false
            }

            toggle {
                configName = "dungeonBreakdown"
                name = "Dungeon Breakdown"
                description = "Sends dungeon info after run"
                default = false
            }
        }
    }

    category( "Msc."){
        subcategory("Block Overlay") {
            toggle {
                configName = "overlayEnabled"
                name = "Render Block Overlay"
                description = "Highlights the block you are looking at"
            }

            colorpicker {
                configName = "blockHighlightColor"
                name = "Block Highlight Color"
                description = "The color to highlight blocks"
                default = rgba(0, 255, 255, 255)
                shouldShow { settings -> settings["overlayEnabled"] as Boolean }
            }

            toggle {
                configName = "fillBlockOverlay"
                name = "Fill blocks"
                description = "Fills the blocks with the color"
                shouldShow { settings -> settings["overlayEnabled"] as Boolean }
            }

            colorpicker {
                configName = "blockFillColor"
                name = "Block Fill Color"
                description = "The color to fill blocks"
                default = rgba(0, 255, 255, 30)
                shouldShow { settings -> settings["overlayEnabled"] as Boolean && settings["fillBlockOverlay"] as Boolean }
            }

            toggle {
                configName = "chromaHighlight"
                name = "Chroma overlay"
                description = "Makes the outline chroma"
                shouldShow { settings -> settings["overlayEnabled"] as Boolean }
            }

            stepslider {
                configName = "chromaOverlaySpeed"
                name = "Chroma Speed"
                description = "The speed of the chroma effect"
                min = 1
                max = 10
                step = 1
                default = 1
                shouldShow { settings ->
                    settings["overlayEnabled"] as Boolean && settings["chromaHighlight"] as Boolean
                }
            }

            stepslider {
                configName = "overlayLineWidth"
                name = "Line width"
                description = "Line width for the outline"
                min = 1
                max = 5
                step = 1
                default = 3
                shouldShow { settings -> settings["overlayEnabled"] as Boolean }
            }
        }

        subcategory("Inventory Buttons"){
            toggle {
                configName = "buttonsEnabled"
                name = "Enabled"
                description = "Enables the inventory buttons"
            }

            button {
                configName = "buttonEdit"
                name = "Inventory Button Editor"
                description = "Opens the inventory button editor"
                placeholder = "Open"

                onclick {
                    Stella.mc.displayGuiScreen(ButtonLayoutEditor())
                }
            }
        }

        subcategory("Pet Display") {
            toggle {
                configName = "petDisplay"
                name = "Enabled"
                description = "Enables the pet display"
            }
        }
    }
}