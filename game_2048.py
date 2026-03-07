#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
2048 Game - Single File Implementation
"""

import random
import os
import sys

# ANSI color codes for terminal display
class Colors:
    RESET = '\033[0m'
    BOLD = '\033[1m'
    RED = '\033[91m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    PURPLE = '\033[95m'
    CYAN = '\033[96m'
    WHITE = '\033[97m'
    
    # Background colors for tiles
    BG_BLACK = '\033[40m'
    BG_RED = '\033[41m'
    BG_GREEN = '\033[42m'
    BG_YELLOW = '\033[43m'
    BG_BLUE = '\033[44m'
    BG_PURPLE = '\033[45m'
    BG_CYAN = '\033[46m'
    BG_WHITE = '\033[47m'

class Game2048:
    """Main 2048 game class"""
    
    def __init__(self):
        """Initialize the game"""
        self.grid = [[0 for _ in range(4)] for _ in range(4)]
        self.score = 0
        self.game_over = False
        self.won = False
        self.initialize_grid()
    
    def initialize_grid(self):
        """Initialize the grid with two random tiles"""
        # Clear grid
        self.grid = [[0 for _ in range(4)] for _ in range(4)]
        self.score = 0
        self.game_over = False
        self.won = False
        
        # Add two initial tiles
        self.add_random_tile()
        self.add_random_tile()
    
    def add_random_tile(self):
        """Add a random tile (2 or 4) to an empty cell"""
        empty_cells = []
        for i in range(4):
            for j in range(4):
                if self.grid[i][j] == 0:
                    empty_cells.append((i, j))
        
        if empty_cells:
            i, j = random.choice(empty_cells)
            # 90% chance for 2, 10% chance for 4
            self.grid[i][j] = 2 if random.random() < 0.9 else 4
            return True
        return False
    
    def get_tile_color(self, value):
        """Get color for tile based on its value"""
        colors = {
            0: (Colors.WHITE, Colors.BG_BLACK),
            2: (Colors.WHITE, Colors.BG_BLUE),
            4: (Colors.WHITE, Colors.BG_CYAN),
            8: (Colors.WHITE, Colors.BG_GREEN),
            16: (Colors.WHITE, Colors.BG_YELLOW),
            32: (Colors.WHITE, Colors.BG_RED),
            64: (Colors.WHITE, Colors.BG_PURPLE),
            128: (Colors.BOLD + Colors.YELLOW, Colors.BG_BLUE),
            256: (Colors.BOLD + Colors.YELLOW, Colors.BG_CYAN),
            512: (Colors.BOLD + Colors.YELLOW, Colors.BG_GREEN),
            1024: (Colors.BOLD + Colors.YELLOW, Colors.BG_RED),
            2048: (Colors.BOLD + Colors.YELLOW, Colors.BG_PURPLE),
        }
        
        if value > 2048:
            return (Colors.BOLD + Colors.RED, Colors.BG_WHITE)
        
        # For values not in dictionary, use the closest lower value
        for key in sorted(colors.keys(), reverse=True):
            if value >= key:
                return colors[key]
        
        return colors[0]
    
    def display_grid(self):
        """Display the grid with colors in terminal"""
        os.system('cls' if os.name == 'nt' else 'clear')
        
        print(f"{Colors.BOLD}{Colors.CYAN}=== 2048 GAME ==={Colors.RESET}")
        print(f"{Colors.GREEN}Score: {self.score}{Colors.RESET}")
        print(f"{Colors.YELLOW}Controls: W/A/S/D or Arrow Keys{Colors.RESET}")
        print(f"{Colors.YELLOW}Quit: Q, Restart: R{Colors.RESET}")
        print()
        
        # Display grid
        print("+" + "-----" * 4 + "+")
        
        for i in range(4):
            print("|", end="")
            for j in range(4):
                value = self.grid[i][j]
                text_color, bg_color = self.get_tile_color(value)
                
                if value == 0:
                    display_value = "     "
                else:
                    display_value = f"{value:^5}"
                
                print(f"{bg_color}{text_color}{display_value}{Colors.RESET}|", end="")
            print()
            print("+" + "-----" * 4 + "+")
        
        print()
        
        # Game status messages
        if self.won:
            print(f"{Colors.BOLD}{Colors.GREEN}🎉 Congratulations! You reached 2048! 🎉{Colors.RESET}")
            print(f"{Colors.YELLOW}Continue playing or press R to restart.{Colors.RESET}")
        elif self.game_over:
            print(f"{Colors.BOLD}{Colors.RED}💀 Game Over! No more moves available. 💀{Colors.RESET}")
            print(f"{Colors.YELLOW}Press R to restart.{Colors.RESET}")
    
    def move_left(self):
        """Move tiles to the left"""
        moved = False
        
        for i in range(4):
            # Remove zeros and combine adjacent equal numbers
            row = [cell for cell in self.grid[i] if cell != 0]
            
            # Combine adjacent equal numbers
            j = 0
            while j < len(row) - 1:
                if row[j] == row[j + 1]:
                    row[j] *= 2
                    self.score += row[j]
                    row.pop(j + 1)
                    
                    # Check for win
                    if row[j] == 2048:
                        self.won = True
                j += 1
            
            # Pad with zeros
            row += [0] * (4 - len(row))
            
            # Check if row changed
            if self.grid[i] != row:
                moved = True
                self.grid[i] = row
        
        return moved
    
    def move_right(self):
        """Move tiles to the right"""
        # Reverse each row, move left, then reverse back
        for i in range(4):
            self.grid[i].reverse()
        
        moved = self.move_left()
        
        for i in range(4):
            self.grid[i].reverse()
        
        return moved
    
    def move_up(self):
        """Move tiles upward"""
        # Transpose grid, move left, then transpose back
        self.transpose_grid()
        moved = self.move_left()
        self.transpose_grid()
        return moved
    
    def move_down(self):
        """Move tiles downward"""
        # Transpose grid, move right, then transpose back
        self.transpose_grid()
        moved = self.move_right()
        self.transpose_grid()
        return moved
    
    def transpose_grid(self):
        """Transpose the grid (swap rows and columns)"""
        self.grid = [[self.grid[j][i] for j in range(4)] for i in range(4)]
    
    def can_move(self):
        """Check if any moves are possible"""
        # Check for empty cells
        for i in range(4):
            for j in range(4):
                if self.grid[i][j] == 0:
                    return True
        
        # Check for possible merges horizontally
        for i in range(4):
            for j in range(3):
                if self.grid[i][j] == self.grid[i][j + 1]:
                    return True
        
        # Check for possible merges vertically
        for i in range(3):
            for j in range(4):
                if self.grid[i][j] == self.grid[i + 1][j]:
                    return True
        
        return False
    
    def check_game_status(self):
        """Check if game is won or lost"""
        # Check for win
        if not self.won:
            for i in range(4):
                for j in range(4):
                    if self.grid[i][j] == 2048:
                        self.won = True
                        return
        
        # Check for game over
        if not self.can_move():
            self.game_over = True
    
    def handle_input(self, key):
        """Handle user input"""
        moved = False
        
        if key in ['w', 'W', '\x1b[A']:  # Up arrow or W
            moved = self.move_up()
        elif key in ['s', 'S', '\x1b[B']:  # Down arrow or S
            moved = self.move_down()
        elif key in ['a', 'A', '\x1b[D']:  # Left arrow or A
            moved = self.move_left()
        elif key in ['d', 'D', '\x1b[C']:  # Right arrow or D
            moved = self.move_right()
        elif key in ['r', 'R']:  # Restart
            self.initialize_grid()
            return True
        elif key in ['q', 'Q']:  # Quit
            print(f"{Colors.YELLOW}Thanks for playing! Final score: {self.score}{Colors.RESET}")
            sys.exit(0)
        else:
            return False
        
        # If a move was made, add a new tile and check game status
        if moved:
            self.add_random_tile()
            self.check_game_status()
        
        return True

# For Windows compatibility with arrow keys
if os.name == 'nt':
    import msvcrt
    
    def get_key_windows():
        """Get key input for Windows"""
        key = msvcrt.getch()
        
        # Handle arrow keys
        if key == b'\xe0':
            next_key = msvcrt.getch()
            arrow_keys = {
                b'H': '\x1b[A',  # Up
                b'P': '\x1b[B',  # Down
                b'K': '\x1b[D',  # Left
                b'M': '\x1b[C'   # Right
            }
            return arrow_keys.get(next_key, '')
        
        return key.decode('utf-8', errors='ignore')
    
    get_key = get_key_windows
else:
    # For Unix/Linux/Mac
    import tty
    import termios
    
    def get_key_unix():
        """Get key input for Unix/Linux/Mac"""
        fd = sys.stdin.fileno()
        old_settings = termios.tcgetattr(fd)
        
        try:
            tty.setraw(sys.stdin.fileno())
            ch = sys.stdin.read(1)
            
            # Handle arrow keys (escape sequences)
            if ch == '\x1b':
                ch += sys.stdin.read(2)  # Read the next two characters
            
            return ch
        finally:
            termios.tcsetattr(fd, termios.TCSADRAIN, old_settings)
    
    get_key = get_key_unix

def main():
    """Main game loop"""
    game = Game2048()
    
    print(f"{Colors.BOLD}{Colors.CYAN}Welcome to 2048!{Colors.RESET}")
    print(f"{Colors.YELLOW}Use W/A/S/D or Arrow Keys to move tiles.{Colors.RESET}")
    print(f"{Colors.YELLOW}Press Q to quit, R to restart.{Colors.RESET}")
    print(f"{Colors.GREEN}Press any key to start...{Colors.RESET}")
    get_key()  # Wait for any key
    
    while True:
        game.display_grid()
        
        # Get user input
        key = get_key()
        game.handle_input(key)

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n{Colors.YELLOW}Game interrupted. Thanks for playing!{Colors.RESET}")
    except Exception as e:
        print(f"{Colors.RED}An error occurred: {e}{Colors.RESET}")
        print(f"{Colors.YELLOW}Make sure your terminal supports ANSI colors.{Colors.RESET}")
