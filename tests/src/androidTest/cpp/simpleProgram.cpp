#include <string>

int main(int argc, const char * argv[]) {
    if (2 == argc) {
    int input = std::stoi(argv[1]);
    return input + input;
  }
  return -1;
}
