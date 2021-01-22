#include <iostream>

int main(int argc, const char * argv[]) {
  if (3 != argc) {
    std::cerr << "Usage: " << argv[0] << " environment_variable_name expected_value" << std::endl;
    return -1;
  }

  const char * expectedValue = argv[2];
  const char * actualValue = getenv(argv[1]);

  if (nullptr == actualValue) {
    return -2;
  }

  return strcmp(expectedValue, actualValue);
}
